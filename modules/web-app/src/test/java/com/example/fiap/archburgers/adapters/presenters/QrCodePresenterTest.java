package com.example.fiap.archburgers.adapters.presenters;//import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class QrCodePresenterTest {

    @Test
    @Disabled
    void renderQrCode() throws IOException {
        /*
        This unit test requires a human for validation :-|
        Better to keep disabled by default
         */
        QrCodePresenter qrCodePresenter = new QrCodePresenter();

        var image = qrCodePresenter.renderQrCode("00020101021243650016COM.MERCADOLIBRE020130636787e9685-7de5-43f1-b09a-6d70f6f6c1e45204000053039865802BR5909Test Test6009SAO PAULO62070503***63043962");

        var output = Paths.get("/tmp/qrcode-test.png");
        Files.write(output, image);
        System.out.println("::::: Output saved to " + output);
    }
}