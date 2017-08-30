/*
 * Copyright 2000-2016 Holon TDCN.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.internal.device;

import com.holonplatform.vaadin.device.UserAgentInspector;
import com.vaadin.server.VaadinRequest;

/**
 * Helper class to obtain device informations using Browser user-agent string data
 * 
 * @since 4.2.0
 */
public class DefaultUserAgentInspector implements UserAgentInspector {

	private static final long serialVersionUID = 4243512097574898067L;

	private static final String engineWebKit = "webkit";

	private static final String deviceIphone = "iphone";
	private static final String deviceIpod = "ipod";
	private static final String deviceIpad = "ipad";
	private static final String deviceMacPpc = "macintosh"; // Used for disambiguation
	private static final String deviceAndroid = "android";
	private static final String deviceGoogleTV = "googletv";
	private static final String deviceHtcFlyer = "htc_flyer";
	private static final String deviceSymbian = "symbian";
	private static final String deviceS60 = "series60";
	private static final String deviceS70 = "series70";
	private static final String deviceS80 = "series80";
	private static final String deviceS90 = "series90";
	private static final String deviceWinPhone7 = "windows phone os 7";
	private static final String deviceWinMob = "windows ce";
	private static final String deviceWindows = "windows";
	private static final String deviceIeMob = "iemobile";
	private static final String devicePpc = "ppc"; // Stands for PocketPC
	private static final String enginePie = "wm5 pie"; // An old Windows Mobile
	private static final String deviceBB = "blackberry";
	private static final String vndRIM = "vnd.rim"; // Detectable when BB devices emulate IE or Firefox
	private static final String deviceBBPlaybook = "playbook"; // PlayBook tablet
	private static final String devicePalm = "palm";
	private static final String deviceWebOS = "webos"; // For Palm's line of WebOS
	private static final String deviceWebOShp = "hpwos"; // For HP's line of WebOS
	private static final String engineBlazer = "blazer"; // Old Palm
	private static final String engineXiino = "xiino"; // Another old Palm
	private static final String deviceKindle = "kindle"; // Amazon Kindle, eInk one.
	private static final String deviceNuvifone = "nuvifone"; // Garmin Nuvifone

	// mobile-specific content
	private static final String vndwap = "vnd.wap";
	private static final String wml = "wml";

	// others
	private static final String deviceTablet = "tablet"; // Generic term for slate and tablet devices
	private static final String deviceBrew = "brew";
	private static final String deviceDanger = "danger";
	private static final String deviceHiptop = "hiptop";
	private static final String devicePlaystation = "playstation";
	private static final String deviceNintendoDs = "nitro";
	private static final String deviceNintendo = "nintendo";
	private static final String deviceWii = "wii";
	private static final String deviceXbox = "xbox";
	private static final String deviceArchos = "archos";

	private static final String engineOpera = "opera"; // Popular browser
	private static final String engineNetfront = "netfront"; // Common embedded OS browser
	private static final String engineUpBrowser = "up.browser"; // common on some phones
	private static final String engineOpenWeb = "openweb"; // Transcoding by OpenWave server
	private static final String deviceMidp = "midp"; // a mobile Java technology
	private static final String uplink = "up.link";

	private static final String devicePda = "pda"; // some devices report themselves as PDAs
	private static final String mini = "mini"; // Some mobile browsers put "mini" in their names.
	private static final String mobile = "mobile"; // Some mobile browsers put "mobile" in their user agent strings.
	private static final String mobi = "mobi"; // Some mobile browsers put "mobi" in their user agent strings.

	private static final String maemo = "maemo";
	private static final String linux = "linux";
	private static final String qtembedded = "qt embedded"; // for Sony Mylo
	private static final String mylocom2 = "com2"; // for Sony Mylo also

	// manufacturer
	private static final String manuSonyEricsson = "sonyericsson";
	private static final String manuericsson = "ericsson";
	private static final String manuSamsung1 = "sec-sgh";
	private static final String manuSony = "sony";
	private static final String manuHtc = "htc"; // Popular Android and WinMo manufacturer

	// operator
	private static final String svcDocomo = "docomo";
	private static final String svcKddi = "kddi";
	private static final String svcVodafone = "vodafone";

	private static final String disUpdate = "update"; // pda vs. update

	/**
	 * User-Agent header
	 */
	private final String userAgentHeader;
	/**
	 * Accept header
	 */
	private final String httpAcceptHeader;

	private final boolean webKit;
	private final boolean iPhone;
	private final boolean iPad;
	private final boolean iPod;
	private final boolean android;
	private final boolean androidPhone;
	private final boolean androidTablet;
	private final boolean windowsPhone;
	private final boolean kindle;
	private final boolean playstation;
	private final boolean nintendo;
	private final boolean xbox;
	private final boolean tablet;
	private final boolean smartphone;
	private final boolean mobileDevice;

	/**
	 * Constructor using {@link VaadinRequest}
	 * @param request Vaadin Request
	 */
	public DefaultUserAgentInspector(VaadinRequest request) {
		this(request.getHeader("user-agent"), request.getHeader("accept"));
	}

	/**
	 * Constructor
	 * @param userAgentHeader the User-Agent header
	 * @param httpAcceptHeader the Accept header
	 */
	public DefaultUserAgentInspector(String userAgentHeader, String httpAcceptHeader) {
		this.userAgentHeader = (userAgentHeader != null) ? userAgentHeader.toLowerCase() : "";
		this.httpAcceptHeader = (httpAcceptHeader != null) ? httpAcceptHeader.toLowerCase() : "";
		// detect
		this.webKit = detectWebkit();
		this.iPad = detectIpad();
		this.iPod = detectIpod();
		this.iPhone = detectIphone();
		this.windowsPhone = detectWindowsMobile() || detectWindowsPhone7();
		this.android = detectAndroid();
		this.androidPhone = detectAndroidPhone();
		this.androidTablet = detectAndroidTablet();
		this.kindle = detectKindle();
		this.playstation = detectSonyPlaystation();
		this.nintendo = detectNintendo();
		this.xbox = detectXbox();
		this.tablet = iPad || androidTablet || detectBlackBerryTablet() || detectWebOSTablet();
		this.smartphone = iPhone || androidPhone || iPod || windowsPhone || detectS60OssBrowser() || detectSymbianOS()
				|| detectBlackBerry() || detectPalmWebOS() || detectPalmOS() || detectGarminNuvifone();
		this.mobileDevice = detectMobile();
	}

	/**
	 * Return the User-Agent header
	 * @return User-agent header in lower case
	 */
	@Override
	public String getUserAgentHeader() {
		return userAgentHeader;
	}

	/**
	 * Return the Accept header
	 * @return Accept header in lower case
	 */
	@Override
	public String getAcceptHeader() {
		return httpAcceptHeader;
	}

	/**
	 * WebKit check
	 * @return whether the current browser is based on WebKit
	 */
	@Override
	public boolean isWebKit() {
		return webKit;
	}

	/**
	 * iPhone check
	 * @return whether the device is an iPhone
	 */
	@Override
	public boolean isIPhone() {
		return iPhone;
	}

	/**
	 * iPad check
	 * @return whether the device is an iPad
	 */
	@Override
	public boolean isIPad() {
		return iPad;
	}

	/**
	 * iPod check
	 * @return whether the device is an iPod touch
	 */
	@Override
	public boolean isIPod() {
		return iPod;
	}

	/**
	 * iOS check
	 * @return whether the device is an iOS device
	 */
	@Override
	public boolean isIOs() {
		return iPhone || iPad || iPod;
	}

	/**
	 * Android check
	 * @return whether the device is an Android device
	 */
	@Override
	public boolean isAndroid() {
		return android;
	}

	/**
	 * Android phone check
	 * @return whether the device is an Android phone
	 */
	@Override
	public boolean isAndroidPhone() {
		return androidPhone;
	}

	/**
	 * Android tablet check
	 * @return whether the device is an Android tablet
	 */
	@Override
	public boolean isAndroidTablet() {
		return androidTablet;
	}

	/**
	 * Windows phone check
	 * @return whether the device is a Windows phone
	 */
	@Override
	public boolean isWindowsPhone() {
		return windowsPhone;
	}

	/**
	 * Kindle check
	 * @return whether the device is a Kindle
	 */
	@Override
	public boolean isKindle() {
		return kindle;
	}

	/**
	 * PlayStation check
	 * @return whether the device is a PlayStation
	 */
	@Override
	public boolean isPlaystation() {
		return playstation;
	}

	/**
	 * Nintendo check
	 * @return whether the device is a Nintendo
	 */
	@Override
	public boolean isNintendo() {
		return nintendo;
	}

	/**
	 * Xbox check
	 * @return whether the device is a Xbox
	 */
	@Override
	public boolean isXbox() {
		return xbox;
	}

	/**
	 * Game console check
	 * @return whether the device is a game console
	 */
	@Override
	public boolean isGameConsole() {
		return playstation || nintendo || xbox;
	}

	/**
	 * Smartphone check
	 * @return whether the device is a Smartphone
	 */
	@Override
	public boolean isSmartphone() {
		return smartphone;
	}

	/**
	 * Tablet check
	 * @return whether the device is a Tablet
	 */
	@Override
	public boolean isTablet() {
		return tablet;
	}

	/**
	 * Mobile device check
	 * @return whether the device is a mobile device
	 */
	@Override
	public boolean isMobile() {
		return mobileDevice;
	}

	// ---------------------------------------------------------------------------

	private boolean detectIphone() {
		return userAgentHeader.indexOf(deviceIphone) != -1 && !iPad && !iPod;
	}

	private boolean detectIpod() {
		return userAgentHeader.indexOf(deviceIpod) != -1;
	}

	private boolean detectIpad() {
		return webKit && userAgentHeader.indexOf(deviceIpad) != -1;
	}

	private boolean detectAndroid() {
		if ((userAgentHeader.indexOf(deviceAndroid) != -1) || detectGoogleTV()) {
			return true;
		}
		// Special check for the HTC Flyer 7" tablet
		if (userAgentHeader.indexOf(deviceHtcFlyer) != -1) {
			return true;
		}
		return false;
	}

	private boolean detectAndroidPhone() {
		if (android && (userAgentHeader.indexOf(mobile) != -1)) {
			return true;
		}
		// Special check for Android phones with Opera Mobile.
		if (detectOperaAndroidPhone()) {
			return true;
		}
		return false;
	}

	private boolean detectAndroidTablet() {
		if (!android) {
			return false;
		}
		// Special check for Opera Android Phones
		if (detectOperaMobile()) {
			return false;
		}
		// Special check for the HTC Flyer 7" tablet
		if (userAgentHeader.indexOf(deviceHtcFlyer) != -1) {
			return false;
		}
		// if it's Android and does NOT have 'mobile' in it, Google says it's a tablet.
		if ((userAgentHeader.indexOf(mobile) > -1)) {
			return false;
		}
		return true;
	}

	private boolean detectGoogleTV() {
		return userAgentHeader.indexOf(deviceGoogleTV) != -1;
	}

	private boolean detectWebkit() {
		return userAgentHeader.indexOf(engineWebKit) != -1;
	}

	private boolean detectS60OssBrowser() {
		return webKit && (userAgentHeader.indexOf(deviceSymbian) != -1 || userAgentHeader.indexOf(deviceS60) != -1);
	}

	private boolean detectSymbianOS() {
		if (userAgentHeader.indexOf(deviceSymbian) != -1 || userAgentHeader.indexOf(deviceS60) != -1
				|| userAgentHeader.indexOf(deviceS70) != -1 || userAgentHeader.indexOf(deviceS80) != -1
				|| userAgentHeader.indexOf(deviceS90) != -1) {
			return true;
		}
		return false;
	}

	private boolean detectWindowsPhone7() {
		return userAgentHeader.indexOf(deviceWinPhone7) != -1;
	}

	private boolean detectWindowsMobile() {
		// Exclude new Windows Phone 7.
		if (detectWindowsPhone7()) {
			return false;
		}
		if (userAgentHeader.indexOf(deviceWinMob) != -1 || userAgentHeader.indexOf(deviceWinMob) != -1
				|| userAgentHeader.indexOf(deviceIeMob) != -1 || userAgentHeader.indexOf(enginePie) != -1
				|| (userAgentHeader.indexOf(manuHtc) != -1 && userAgentHeader.indexOf(deviceWindows) != -1)
				|| (detectWapWml() && userAgentHeader.indexOf(deviceWindows) != -1)) {
			return true;
		}
		if (userAgentHeader.indexOf(devicePpc) != -1 && !(userAgentHeader.indexOf(deviceMacPpc) != -1)) {
			return true;
		}
		return false;
	}

	private boolean detectBlackBerry() {
		return userAgentHeader.indexOf(deviceBB) != -1 || httpAcceptHeader.indexOf(vndRIM) != -1;
	}

	private boolean detectBlackBerryTablet() {
		return userAgentHeader.indexOf(deviceBBPlaybook) != -1;
	}

	private boolean detectPalmOS() {
		if (userAgentHeader.indexOf(devicePalm) != -1 || userAgentHeader.indexOf(engineBlazer) != -1
				|| userAgentHeader.indexOf(engineXiino) != -1) {
			// Make sure it's not WebOS first
			if (detectPalmWebOS()) {
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean detectPalmWebOS() {
		return userAgentHeader.indexOf(deviceWebOS) != -1;
	}

	private boolean detectWebOSTablet() {
		if (userAgentHeader.indexOf(deviceWebOShp) != -1 && userAgentHeader.indexOf(deviceTablet) != -1) {
			return true;
		}
		return false;
	}

	private boolean detectGarminNuvifone() {
		return userAgentHeader.indexOf(deviceNuvifone) != -1;
	}

	private boolean detectBrewDevice() {
		return userAgentHeader.indexOf(deviceBrew) != -1;
	}

	private boolean detectDangerHiptop() {
		return userAgentHeader.indexOf(deviceDanger) != -1 || userAgentHeader.indexOf(deviceHiptop) != -1;
	}

	private boolean detectOperaMobile() {
		return userAgentHeader.indexOf(engineOpera) != -1
				&& (userAgentHeader.indexOf(mini) != -1 || userAgentHeader.indexOf(mobi) != -1);
	}

	private boolean detectOperaAndroidPhone() {
		return userAgentHeader.indexOf(engineOpera) != -1
				&& (userAgentHeader.indexOf(deviceAndroid) != -1 && userAgentHeader.indexOf(mobi) != -1);
	}

	private boolean detectWapWml() {
		return httpAcceptHeader.indexOf(vndwap) != -1 || httpAcceptHeader.indexOf(wml) != -1;
	}

	private boolean detectKindle() {
		return userAgentHeader.indexOf(deviceKindle) != -1 && !detectAndroid();
	}

	private boolean detectMobile() {
		if (smartphone) {
			return true;
		}
		if (isGameConsole()) {
			return true;
		}
		if (detectWapWml() || detectBrewDevice() || detectOperaMobile() || detectSonyMylo()) {
			return true;
		}
		if ((userAgentHeader.indexOf(engineNetfront) != -1) || (userAgentHeader.indexOf(engineUpBrowser) != -1)
				|| (userAgentHeader.indexOf(engineOpenWeb) != -1)) {
			return true;
		}
		if (detectDangerHiptop() || detectMidpCapable() || detectMaemoTablet() || detectArchos()) {
			return true;
		}
		if ((userAgentHeader.indexOf(devicePda) != -1) && (userAgentHeader.indexOf(disUpdate) < 0)) {
			return true;
		}
		if (userAgentHeader.indexOf(mobile) != -1) {
			return true;
		}
		// older phones
		if (userAgentHeader.indexOf(uplink) != -1) {
			return true;
		}
		if (userAgentHeader.indexOf(manuSonyEricsson) != -1) {
			return true;
		}
		if (userAgentHeader.indexOf(manuericsson) != -1) {
			return true;
		}
		if (userAgentHeader.indexOf(manuSamsung1) != -1) {
			return true;
		}
		if (userAgentHeader.indexOf(svcDocomo) != -1) {
			return true;
		}
		if (userAgentHeader.indexOf(svcKddi) != -1) {
			return true;
		}
		if (userAgentHeader.indexOf(svcVodafone) != -1) {
			return true;
		}
		return false;
	}

	private boolean detectSonyPlaystation() {
		return userAgentHeader.indexOf(devicePlaystation) != -1;
	}

	private boolean detectNintendo() {
		return userAgentHeader.indexOf(deviceNintendo) != -1 || userAgentHeader.indexOf(deviceWii) != -1
				|| userAgentHeader.indexOf(deviceNintendoDs) != -1;
	}

	private boolean detectXbox() {
		return userAgentHeader.indexOf(deviceXbox) != -1;
	}

	private boolean detectMidpCapable() {
		if (userAgentHeader.indexOf(deviceMidp) != -1 || httpAcceptHeader.indexOf(deviceMidp) != -1) {
			return true;
		}
		return false;
	}

	private boolean detectMaemoTablet() {
		if (userAgentHeader.indexOf(maemo) != -1) {
			return true;
		} else if (userAgentHeader.indexOf(linux) != -1 && userAgentHeader.indexOf(deviceTablet) != -1
				&& !detectWebOSTablet() && !detectAndroid()) {
			return true;
		}
		return false;
	}

	private boolean detectArchos() {
		return userAgentHeader.indexOf(deviceArchos) != -1;
	}

	private boolean detectSonyMylo() {
		if (userAgentHeader.indexOf(manuSony) != -1
				&& (userAgentHeader.indexOf(qtembedded) != -1 || userAgentHeader.indexOf(mylocom2) != -1)) {
			return true;
		}
		return false;
	}

}