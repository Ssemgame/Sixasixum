package yasuo.apps.sixasixum;

import android.content.Context;
import android.hardware.usb.*;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inicializace USB managera
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        // najdeme PS3 Controller
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice dev : deviceList.values()) {
            if (dev.getVendorId() == 1356 && dev.getProductId() == 616) {
                device = dev;
                break;
            }
        }

        if (device == null) {
            // PS3 Controller není připojen
            return;
        }

        // otevření připojení k ovladači
        connection = usbManager.openDevice(device);
        if (connection == null) {
            // chyba při otevírání připojení
            return;
        }

        // nalezení rozhraní pro čtení dat
        UsbInterface intf = device.getInterface(0);
        UsbEndpoint endpoint = intf.getEndpoint(0);

        // připojení k rozhraní
        connection.claimInterface(intf, true);

        // nekonečná smyčka pro čtení dat z ovladače
        while (true) {
            byte[] data = new byte[endpoint.getMaxPacketSize()];
            int count = connection.bulkTransfer(endpoint, data, data.length, 1000);

            if (count > 0) {
                // zpracujeme data a zkontrolujeme, které tlačítko bylo stisknuto
                if ((data[2] & 0xFF) == 0x01) {
                    // tlačítko X na ovladači PS3 bylo stisknuto
                    // vykonat příslušnou akci
                } else if ((data[2] & 0xFF) == 0x02) {
                    // tlačítko Y na ovladači PS3 bylo stisknuto
                    // vykonat příslušnou akci
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (connection != null) {
            // uvolníme připojení k ovladači
            connection.releaseInterface(device.getInterface(0));
            connection.close();
        }
    }
}
