package com.example.final_project_group5.activity;

import static com.example.final_project_group5.config.PlatformPositioningProvider.LOG_TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.final_project_group5.R;
import com.example.final_project_group5.config.PlatformPositioningProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoCoordinatesUpdate;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.engine.AuthenticationMode;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.LineCap;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapCamera;
import com.here.sdk.mapview.MapCameraAnimation;
import com.here.sdk.mapview.MapCameraAnimationFactory;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapMeasureDependentRenderSize;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.RenderSize;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;
import com.here.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MapActivity extends AppCompatActivity {
    MapView mapView;
    FloatingActionButton btnFlyTo, btnCalculateRoute, btnCurrentLocation, btnBack; // Thêm btnBack
    MapCamera mapCamera;
    PlatformPositioningProvider platformPositioningProvider;
    RoutingEngine routingEngine;
    MapPolyline currentRoutePolyline;
    GeoCoordinates startGeoCoordinates, storeLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHERESDK();

        setContentView(R.layout.activity_map);

        // Khởi tạo các view từ layout
        mapView = findViewById(R.id.map_view);
        btnFlyTo = findViewById(R.id.btn_fly_to);
        btnCurrentLocation = findViewById(R.id.btn_current_location);
        btnCalculateRoute = findViewById(R.id.btn_calculate_route);
        btnBack = findViewById(R.id.btn_back); // Khởi tạo nút Back

        mapView.onCreate(savedInstanceState);
        mapCamera = mapView.getCamera();
        loadMapScene();

        // Khởi tạo vị trí cửa hàng và đánh dấu bằng marker
        storeLocation = new GeoCoordinates(10.845349, 106.839597);
        MapImage mapImage = MapImageFactory.fromResource(this.getResources(), R.drawable.location);
        MapMarker mapMarker = new MapMarker(storeLocation, mapImage);
        mapView.getMapScene().addMapMarker(mapMarker);

        // Sự kiện click cho các nút FloatingActionButton
        btnFlyTo.setOnClickListener(v -> flyTo(storeLocation, mapCamera));
        btnCurrentLocation.setOnClickListener(v -> flyTo(startGeoCoordinates, mapCamera));
        btnCalculateRoute.setOnClickListener(v -> calculateRoutes(startGeoCoordinates, storeLocation));

        // Xử lý nút Back để quay về trang chủ
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, UserDashboard.class); // Giả sử MainActivity là trang chủ
            startActivity(intent);
            finish(); // Kết thúc MapActivity để không quay lại khi nhấn nút Back từ trang chủ
        });

        // Khởi tạo và bắt đầu cập nhật vị trí
        platformPositioningProvider = new PlatformPositioningProvider(this);
        startLocationUpdates();
    }

    // Khởi tạo HERE SDK
    private void initializeHERESDK() {
        String accessKeyID = "756UWS8Iw5nqtxUjNI67ig";
        String accessKeySecret = "bNywvCCS5Q3wImcl9_hfgCuD1YhzqBWuugpB6P02WhxQLwpG8f9BfUGix7FN2I5wreKifMmYJNtnvz7oSiMaPw";
        AuthenticationMode authenticationMode = AuthenticationMode.withKeySecret(accessKeyID, accessKeySecret);
        SDKOptions options = new SDKOptions(authenticationMode);

        try {
            Context context = this;
            SDKNativeEngine.makeSharedInstance(context, options);
            routingEngine = new RoutingEngine();
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    private void loadMapScene() {
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    double distanceInMeters = 1000;
                    MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE_IN_METERS, distanceInMeters);
                    mapView.getCamera().lookAt(new GeoCoordinates(10.845349, 106.839597), mapMeasureZoom);
                } else {
                    Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
                }
            }
        });
    }

    private void disposeHERESDK() {
        SDKNativeEngine sdkNativeEngine = SDKNativeEngine.getSharedInstance();
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose();
            SDKNativeEngine.setSharedInstance(null);
        }
    }

    private void flyTo(GeoCoordinates geoCoordinates, MapCamera mapCamera) {
        if (geoCoordinates == null) {
            Toast.makeText(this, "Không tìm thấy vị trí", Toast.LENGTH_SHORT).show();
            return;
        }
        GeoCoordinatesUpdate geoCoordinatesUpdate = new GeoCoordinatesUpdate(geoCoordinates);
        double bowFactor = 1;
        MapCameraAnimation animation = MapCameraAnimationFactory.flyTo(geoCoordinatesUpdate, bowFactor, Duration.ofSeconds(1));
        mapCamera.startAnimation(animation);
    }

    private void addLocationIndicator(GeoCoordinates geoCoordinates) {
        LocationIndicator locationIndicator = new LocationIndicator();
        locationIndicator.setLocationIndicatorStyle(LocationIndicator.IndicatorStyle.PEDESTRIAN);

        com.here.sdk.core.Location location = new com.here.sdk.core.Location(geoCoordinates);
        location.time = new Date();
        location.bearingInDegrees = 0.0;

        locationIndicator.updateLocation(location);
        locationIndicator.enable(mapView);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }
        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                Log.d(LOG_TAG, "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
                GeoCoordinates geoCoordinates = new GeoCoordinates(location.getLatitude(), location.getLongitude());
                addLocationIndicator(geoCoordinates);
                startGeoCoordinates = geoCoordinates;
            }
        });
    }

    public void calculateRoutes(GeoCoordinates startGeoCoordinates, GeoCoordinates destinationGeoCoordinates) {
        if (startGeoCoordinates == null || destinationGeoCoordinates == null) {
            Toast.makeText(this, "Không thể tính đường do thiếu vị trí", Toast.LENGTH_SHORT).show();
            return;
        }
        Waypoint startWaypoint = new Waypoint(startGeoCoordinates);
        Waypoint destinationWaypoint = new Waypoint(destinationGeoCoordinates);

        List<Waypoint> waypoints = new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

        routingEngine.calculateRoute(waypoints, new CarOptions(), new CalculateRouteCallback() {
            @Override
            public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                if (routingError == null) {
                    Route route = routes.get(0);
                    showRouteOnMap(route);
                } else {
                    Toast.makeText(MapActivity.this, "Lỗi khi tính đường: " + routingError.name(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showRouteOnMap(Route route) {
        if (currentRoutePolyline != null) {
            mapView.getMapScene().removeMapPolyline(currentRoutePolyline);
        }
        GeoPolyline routeGeoPolyline = route.getGeometry();
        float widthInPixels = 20;
        Color polylineColor = Color.valueOf(0, 0.56f, 0.54f, 0.63f);
        mapCamera.lookAt(startGeoCoordinates);
        mapCamera.zoomTo(10);
        try {
            currentRoutePolyline = new MapPolyline(routeGeoPolyline, new MapPolyline.SolidRepresentation(
                    new MapMeasureDependentRenderSize(RenderSize.Unit.PIXELS, widthInPixels),
                    polylineColor,
                    LineCap.ROUND));
            mapView.getMapScene().addMapPolyline(currentRoutePolyline);
        } catch (MapPolyline.Representation.InstantiationException e) {
            Log.e("MapPolyline Representation Exception:", e.error.name());
        } catch (MapMeasureDependentRenderSize.InstantiationException e) {
            Log.e("MapMeasureDependentRenderSize Exception:", e.error.name());
        }
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        disposeHERESDK();
        super.onDestroy();
        platformPositioningProvider.stopLocating();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}