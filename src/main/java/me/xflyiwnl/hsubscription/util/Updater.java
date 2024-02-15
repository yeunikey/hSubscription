package me.xflyiwnl.hsubscription.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.xflyiwnl.hsubscription.HSubscription;
import me.xflyiwnl.hsubscription.request.GetRequest;
import org.bukkit.Bukkit;
import org.checkerframework.checker.index.qual.HasSubsequence;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Updater {

    public static void check() {

        HttpResponse<String> request = new GetRequest()
                .url("https://api.github.com/repos/xflyiwnl/hSubscription/releases")
                .send();

        JsonArray array = new JsonParser().parse(request.body()).getAsJsonArray();

        if (array.size() == 0) return;

        String version = array.get(0).getAsJsonObject().get("tag_name").getAsString();

        if (version.equalsIgnoreCase(HSubscription.getInstance().getDescription().getVersion())) {
            System.out.println(Translator.ofString("update-last")
                    .replace("%version%", version));
        } else {
            System.out.println(Translator.ofString("need-update")
                    .replace("%url%", "https://github.com/xflyiwnl/hSubscription/releases/tag/" + version));
        }

    }

}
