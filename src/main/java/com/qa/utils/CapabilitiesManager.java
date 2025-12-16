package com.qa.utils;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class CapabilitiesManager {
    TestUtils utils = new TestUtils();

    public DesiredCapabilities getCaps() throws IOException {
        GlobalParams params = new GlobalParams();
        Properties props = new PropertyManager().getProps();

        try{
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", params.getPlatformName());
            caps.setCapability("appium:udid", params.getUDID());
            caps.setCapability("appium:deviceName", params.getDeviceName());

            switch(params.getPlatformName()){
                case "Android":
                    caps.setCapability("appium:automationName", props.getProperty("androidAutomationName"));
                    caps.setCapability("appium:appPackage", props.getProperty("androidAppPackage"));
                    caps.setCapability("appium:appActivity", props.getProperty("androidAppActivity"));
                    caps.setCapability("appium:systemPort", params.getSystemPort());
                    caps.setCapability("appium:chromeDriverPort", params.getChromeDriverPort());
                    //String androidAppUrl = getClass().getResource(props.getProperty("androidAppLocation")).getFile();
                    String androidAppUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                            + File.separator + "resources" + File.separator + "apps" + File.separator + "Android.SauceLabs.Mobile.Sample.app.2.7.1.apk";
                    utils.log().info("appUrl is" + androidAppUrl);
                    caps.setCapability("appium:app", androidAppUrl);
                    break;
                case "iOS":
                    caps.setCapability("appium:automationName", props.getProperty("iOSAutomationName"));
                    //String iOSAppUrl = getClass().getResource(props.getProperty("iOSAppLocation")).getFile();
                    String iOSAppUrl = System.getProperty("user.dir") + File.separator + "src" + File.separator + "test"
                            + File.separator + "resources" + File.separator + "apps" + File.separator + "SwagLabsMobileApp.app";
                    utils.log().info("appUrl is" + iOSAppUrl);
                    caps.setCapability("appium:bundleId", props.getProperty("iOSBundleId"));
                    caps.setCapability("appium:wdaLocalPort", params.getWdaLocalPort());
                    caps.setCapability("appium:webkitDebugProxyPort", params.getWebkitDebugProxyPort());
                    caps.setCapability("appium:app", iOSAppUrl);
                    break;
            }
            return caps;
        } catch(Exception e){
            e.printStackTrace();
            utils.log().fatal("Failed to load capabilities. ABORT!!" + e.toString());
            throw e;
        }
    }
}
