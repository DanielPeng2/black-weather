package net.danielpeng.azure;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;
    private FusedLocationProviderClient mFusedLocationClient;
    private double mLatitude;
    private double mLongitude;

    @BindView(R.id.locationLabel)
    TextView mLocationLabel;
    @BindView(R.id.preLabel)
    TextView mPreLabel;
    @BindView(R.id.temperatureLabel)
    TextView mTemperatureLabel;
    @BindView(R.id.humidityValue)
    TextView mHumidityValue;
    @BindView(R.id.precipValue)
    TextView mPrecipValue;
    @BindView(R.id.summaryLabel)
    TextView mSummaryLabel;
    @BindView(R.id.iconImageView)
    ImageView mIconImageView;
    @BindView(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.feelsLabel)
    TextView mFeelsLabel;
    @BindView(R.id.flavourLabel)
    TextView mFlavourLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getPermissions();
        getLocation();

        mProgressBar.setVisibility(View.INVISIBLE);

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                getForecast(mLatitude, mLongitude);
            }
        });

        getForecast(mLatitude, mLongitude);
    }

    private void getForecast(double latitude, double longitude) {
        if (latitude != 0) {
            String apiKey = "b68849dc1488416dbd400d0b059ed4a5";
            String forecastUrl = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude + "?units=ca";
            Log.d(TAG, forecastUrl);

            if (isNetworkAvailable()) {
                toggleRefresh();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(forecastUrl)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleRefresh();
                            }
                        });
                        alertUserAboutError();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                toggleRefresh();
                            }
                        });
                        try {

                            String jsonData = response.body().string();
                            //Log.v(TAG, jsonData);

                            if (response.isSuccessful()) {
                                mCurrentWeather = getCurrentDetails(jsonData);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateDisplay();
                                    }
                                });
                            } else {
                                alertUserAboutError();
                            }

                        } catch (IOException e) {
                            Log.e(TAG, "Exception caught:", e);
                        } catch (JSONException e) {
                            Log.e(TAG, "Exception caught:", e);
                        }
                    }
                });
            } else {
                Toast.makeText(this, R.string.network_unavailable_message, Toast.LENGTH_LONG).show();
            }

        } else {
            final double lat = mLatitude;
            final double longi = mLongitude;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getForecast(lat, longi);
                }
            }, 50);
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mCurrentWeather.setFeelsAndFlavourText();
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mPreLabel.setText(mCurrentWeather.getPreText() + "");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "%");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary().toLowerCase());
        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
        mLocationLabel.setText(hereLocation(mLatitude, mLongitude));
        mFeelsLabel.setText(mCurrentWeather.getFeelsText() + "");
        mFlavourLabel.setText(mCurrentWeather.getFlavourText() + "");
        updateColours();
    }

    private void updateColours() {
        String summaryText = mCurrentWeather.getSummary();
        String feelsText = mCurrentWeather.getFeelsText();

        if (summaryText.contains("Cloudy") || summaryText.contains("Overcast")) {
            mSummaryLabel.setTextColor(Color.parseColor("#607D8B"));
        } else if (summaryText.contains("Rain") || summaryText.contains("Drizzle")) {
            mSummaryLabel.setTextColor(Color.parseColor("#0066FF"));
        } else if (summaryText.contains("Snow")) {
            mSummaryLabel.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (summaryText.contains("Breezy")) {
            mSummaryLabel.setTextColor(Color.parseColor("#4CAF50"));
        } else if (summaryText.contains("Foggy")) {
            mSummaryLabel.setTextColor(Color.parseColor("#9E9E9E"));
        } else if (summaryText.contains("Clear")) {
            mSummaryLabel.setTextColor(Color.parseColor("#03A9F4"));
        } else {
            mSummaryLabel.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (feelsText.contains("windy")) {
            mFeelsLabel.setTextColor(Color.parseColor("#4CAF50"));
        } else if (feelsText.contains("cold") ||
                feelsText.contains("cool") ||
                feelsText.contains("freezing") ||
                feelsText.contains("frosty")) {
            mFeelsLabel.setTextColor(Color.parseColor("#0066FF"));
        } else if (feelsText.contains("cool") || feelsText.contains("nice")) {
            mFeelsLabel.setTextColor(Color.parseColor("#03A9F4"));
        } else if (feelsText.contains("hot") || feelsText.contains("warm")) {
            mFeelsLabel.setTextColor(Color.parseColor("#F44336"));
        }
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);
        currentWeather.setFeelsTemp(currently.getDouble("apparentTemperature"));
        currentWeather.setWindSpeed(currently.getDouble("windSpeed"));

        //Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                }
            }
        });
    }

    public String hereLocation(double lat, double lon) {
        String city = "";
        String country = "";

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);
            if (addressList.size() > 0) {
                city = addressList.get(0).getLocality();
                country = addressList.get(0).getCountryCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return city + ", " + country;
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }
}
