package me.xflyiwnl.hsubscription.request;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public interface Request {

    HttpResponse<String> send();
    CompletableFuture<HttpResponse<String>> sendAsync();

}
