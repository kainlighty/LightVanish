package ru.kainlight.lightvanish.UTILS;

import com.google.gson.*;
import ru.kainlight.lightvanish.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class GitHubUpdater {
    private final Main plugin;

    public GitHubUpdater(Main plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.latestVersion = plugin.getDescription().getVersion();
        this.notification = plugin.getConfig().getBoolean("update-notification");
    }

    private final String currentVersion;
    private String latestVersion;
    private boolean isPrerelease;
    private String resourceURL;
    private final boolean notification;

    private boolean checkForUpdates() {
        String pluginName = plugin.getDescription().getName();
        String githubRepo = "kainlighty/" + pluginName;
        try {
            String url = "https://api.github.com/repos/" + githubRepo + "/releases";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 404) return false;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            JsonArray jsonArray = JsonParser.parseString(responseBuilder.toString()).getAsJsonArray();
            JsonObject latestRelease = jsonArray.get(0).getAsJsonObject();  // Get the first release (the latest one)

            latestVersion = latestRelease.get("tag_name").getAsString();
            resourceURL = latestRelease.get("html_url").getAsString();

            // Get whether the latest release is a pre-release
            isPrerelease = latestRelease.get("prerelease").getAsBoolean();

            String[] currentVersionParts = currentVersion.split("\\-");
            String[] latestVersionParts = latestVersion.split("\\-");

            // Check major.minor.patch version
            String[] currentVersionNumbers = currentVersionParts[0].split("\\.");
            String[] latestVersionNumbers = latestVersionParts[0].split("\\.");

            for (int i = 0; i < Math.min(currentVersionNumbers.length, latestVersionNumbers.length); i++) {
                int currentPart = Integer.parseInt(currentVersionNumbers[i]);
                int latestPart = Integer.parseInt(latestVersionNumbers[i]);

                if (currentPart < latestPart) {
                    return true;
                } else if (currentPart > latestPart) {
                    return false;
                }
            }

            // If major.minor.patch versions are equal and the latest version is a pre-release while the current one isn't, the latest version isn't considered newer
            if (latestVersionParts.length > 1 && currentVersionParts.length == 1) {
                return false;
            }

            // If current version is a prerelease and the latest version is not, the latest version is considered newer
            if (currentVersionParts.length > 1 && latestVersionParts.length == 1) {
                return true;
            }

            // If major.minor.patch versions are equal and both are pre-releases, consider them equal
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Couldn't check for updates! Stacktrace:");
            e.printStackTrace();
            return false;
        }
    }

    public void start() {
        if (!notification) return;

        boolean isUpdateAvailable = checkForUpdates();
        if (isUpdateAvailable) {
            String updateVersion = latestVersion;
            if (isPrerelease) {
                updateVersion += " #d29922(Pre-release)";
            }
            plugin.getParser()
                    .logger("&c ! New version found: " + updateVersion)
                    .logger("&c ! Recommended for installation: &e" + resourceURL);
        }
    }
}
