package net.danielpeng.azure;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CurrentWeather {
    private String mIcon;
    private Long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private double mFeelsTemp;
    private double mWindSpeed;
    private String mSummary;
    private String mTimeZone;
    private String mFeelsText;
    private String mFlavourText;
    private String mPreText;

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        int iconId = R.drawable.clear_day;

        if (mIcon.equals("clear-day")) {
            iconId = R.drawable.clear_day;
        } else if (mIcon.equals("clear-night")) {
            iconId = R.drawable.clear_night;
        } else if (mIcon.equals("rain")) {
            iconId = R.drawable.rain;
        } else if (mIcon.equals("snow")) {
            iconId = R.drawable.snow;
        } else if (mIcon.equals("sleet")) {
            iconId = R.drawable.sleet;
        } else if (mIcon.equals("wind")) {
            iconId = R.drawable.wind;
        } else if (mIcon.equals("fog")) {
            iconId = R.drawable.fog;
        } else if (mIcon.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        } else if (mIcon.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        } else if (mIcon.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }

    public Long getTime() {
        return mTime;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getTime() * 1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public void setTime(Long time) {
        mTime = time;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public int getHumidity() {
        double humidPercentage = mHumidity * 100;
        return (int) Math.round(humidPercentage);
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int) Math.round(precipPercentage);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public double getFeelsTemp() {
        return mFeelsTemp;
    }

    public void setFeelsTemp(double feelsTemp) {
        mFeelsTemp = feelsTemp;
    }

    public double getWindSpeed() {
        return mWindSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getFeelsText() {
        return mFeelsText;
    }

    public String getFlavourText() {
        return mFlavourText;
    }

    public void setFeelsAndFlavourText() {
        if (mWindSpeed > 35) {
            mFeelsText = "hecking windy";
            mFlavourText = "Don't get blown over.";
        } else if (mFeelsTemp <= -15) {
            mFeelsText = "cold as hell";
            mFlavourText = "Don't go outside.";
        } else if (mFeelsTemp <= -10) {
            mFeelsText = "freezing";
            mFlavourText = "Avoid the outdoors.";
        } else if (mFeelsTemp <= 0) {
            mFeelsText = "frosty";
            mFlavourText = "Go do wintery things.";
        } else if (mFeelsTemp <= 10) {
            mFeelsText = "pretty cold";
            mFlavourText = "Put on a jacket.";
        } else if (mFeelsTemp <= 15) {
            mFeelsText = "comfortably cool";
            mFlavourText = "Perfect sweater weather.";
        } else if (mFeelsTemp <= 20) {
            mFeelsText = "really nice";
            mFlavourText = "It's just nice out.";
        } else if (mFeelsTemp <= 25) {
            mFeelsText = "warm";
            mFlavourText = "Break out the shorts.";
        } else if (mFeelsTemp <= 30) {
            mFeelsText = "hot";
            mFlavourText = "Remember sunscreen.";
        } else {
            mFeelsText = "hot as hell";
            mFlavourText = "Heat stroke is upon you.";
        }

        if (mSummary.equals("Rain")) {
            mFlavourText = "Bring an umbrella.";
        } else if (mSummary.equals("Light Rain") || mSummary.equals("Drizzle")) {
            mFlavourText = "Rain jackets are useful.";
        } else if (mSummary.equals("Heavy Rain")) {
            mFlavourText = "Hope you own a boat.";
        } else if (mSummary.equals("Snow")) {
            mFlavourText = "You're a special snowflake :)";
        } else if (mSummary.equals("Heavy Snow")) {
            mFlavourText = "Prepare to dig yourself out.";
        }
    }

    public String getPreText() {
        if (mSummary.contains("Rain") || mSummary.contains("Snow") || mSummary.contains("Drizzle")) {
            mPreText = "There is";
        } else {
            mPreText = "It is currently";
        }
        return mPreText;
    }

    private double convertToCelsius(double temp) {
        return (temp - 32) * 5/9;
    }
}
