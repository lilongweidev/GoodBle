package com.llw.goodble.ble

import java.util.*

object BleUtils {

    private val generic = "-0000-1000-8000-00805F9B34FB"

    /**
     * 获取蓝牙服务名称
     * @param uuid UUID
     */
    fun getServiceName(uuid: UUID) =
        when ("0x${uuid.toString().substring(4, 8).uppercase(Locale.getDefault())}") {
            "0x1800" -> "Generic Access service"
            "0x1801" -> "Generic Attribute service"
            "0x1802" -> "Immediate Alert service"
            "0x1803" -> "Link Loss service"
            "0x1804" -> "Tx Power service"
            "0x1805" -> "Current Time service"
            "0x1806" -> "Reference Time Update service"
            "0x1807" -> "Next DST Change service"
            "0x1808" -> "Glucose service"
            "0x1809" -> "Health Thermometer service"
            "0x180A" -> "Device Information service"
            "0x180D" -> "Heart Rate service"
            "0x180E" -> "Phone Alert Status service"
            "0x180F" -> "Battery service"
            "0x1810" -> "Blood Pressure service"
            "0x1811" -> "Alert Notification service"
            "0x1812" -> "Human Interface Device service"
            "0x1813" -> "Scan Parameters service"
            "0x1814" -> "Running Speed and Cadence service"
            "0x1815" -> "Automation IO service"
            "0x1816" -> "Cycling Speed and Cadence service"
            "0x1818" -> "Cycling Power service"
            "0x1819" -> "Location and Navigation service"
            "0x181A" -> "Environmental Sensing service"
            "0x181B" -> "Body Composition service"
            "0x181C" -> "User Data service"
            "0x181D" -> "Weight Scale service"
            "0x181E" -> "Bond Management service"
            "0x181F" -> "Continuous Glucose Monitoring service"
            "0x1820" -> "Internet Protocol Support service"
            "0x1821" -> "Indoor Positioning service"
            "0x1822" -> "Pulse Oximeter service"
            "0x1823" -> "HTTP Proxy service"
            "0x1824" -> "Transport Discovery service"
            "0x1825" -> "Object Transfer service"
            "0x1826" -> "Fitness Machine service"
            "0x1827" -> "Mesh Provisioning service"
            "0x1828" -> "Mesh Proxy service"
            "0x1829" -> "Reconnection Configuration service"
            "0x183A" -> "Insulin Delivery service"
            "0x183B" -> "Binary Sensor service"
            "0x183C" -> "Emergency Configuration service"
            "0x183D" -> "Authorization Control service"
            "0x183E" -> "Physical Activity Monitor service"
            "0x183F" -> "Elapsed Time service"
            "0x1840" -> "Generic Health Sensor service"
            "0x1843" -> "Audio Input Control service"
            "0x1844" -> "Volume Control service"
            "0x1845" -> "Volume Offset Control service"
            "0x1846" -> "Coordinated Set Identification service"
            "0x1847" -> "Device Time service"
            "0x1848" -> "Media Control service"
            "0x1849" -> "Generic Media Control service"
            "0x184A" -> "Constant Tone Extension service"
            "0x184B" -> "Telephone Bearer service"
            "0x184C" -> "Generic Telephone Bearer service"
            "0x184D" -> "Microphone Control service"
            "0x184E" -> "Audio Stream Control service"
            "0x184F" -> "Broadcast Audio Scan service"
            "0x1850" -> "Published Audio Capabilities service"
            "0x1851" -> "Basic Audio Announcement service"
            "0x1852" -> "Broadcast Audio Announcement service"
            "0x1853" -> "Common Audio service"
            "0x1854" -> "Hearing Access service"
            "0x1855" -> "Telephony and Media Audio service"
            "0x1856" -> "Public Broadcast Announcement service"
            "0x1857" -> "Electronic Shelf Label service"
            else -> "Unknown Service"
        }

    /**
     * 获取特性名称
     * @param uuid UUID
     */
    fun getCharacteristicsName(uuid: UUID) =
        when ("0x${uuid.toString().substring(4, 8).uppercase(Locale.getDefault())}") {
            "0x2A00" -> "Device Name"
            "0x2A01" -> "Appearance"
            "0x2A02" -> "Peripheral Privacy Flag"
            "0x2A03" -> "Reconnection Address"
            "0x2A04" -> "Peripheral Preferred Connection Parameters"
            "0x2A05" -> "Service Changed"
            "0x2A06" -> "Alert Level"
            "0x2A07" -> "Tx Power Level"
            "0x2A08" -> "Date Time"
            "0x2A09" -> "Day of Week"
            "0x2A0A" -> "Day Date Time"
            "0x2A0C" -> "Exact Time 256"
            "0x2A0D" -> "DST Offset"
            "0x2A0E" -> "Time Zone"
            "0x2A0F" -> "Local Time Information"
            "0x2A11" -> "Time with DST"
            "0x2A12" -> "Time Accuracy"
            "0x2A13" -> "Time Source"
            "0x2A14" -> "Reference Time Information"
            "0x2A16" -> "Time Update Control Point"
            "0x2A17" -> "Time Update State"
            "0x2A18" -> "Glucose Measurement"
            "0x2A19" -> "Battery Level"
            "0x2A1C" -> "Temperature Measurement"
            "0x2A1D" -> "Temperature Type"
            "0x2A1E" -> "Intermediate Temperature"
            "0x2A21" -> "Measurement Interval"
            "0x2A22" -> "Boot Keyboard Input Report"
            "0x2A23" -> "System ID"
            "0x2A24" -> "Model Number String"
            "0x2A25" -> "Serial Number String"
            "0x2A26" -> "Firmware Revision String"
            "0x2A27" -> "Hardware Revision String"
            "0x2A28" -> "Software Revision String"
            "0x2A29" -> "Manufacturer Name String"
            "0x2A2A" -> "IEEE 11073-20601 Regulatory Certification Data List"
            "0x2A2B" -> "Current Time"
            "0x2A2C" -> "Magnetic Declination"
            "0x2A31" -> "Scan Refresh"
            "0x2A32" -> "Boot Keyboard Output Report"
            "0x2A33" -> "Boot Mouse Input Report"
            "0x2A34" -> "Glucose Measurement Context"
            "0x2A35" -> "Blood Pressure Measurement"
            "0x2A36" -> "Intermediate Cuff Pressure"
            "0x2A37" -> "Heart Rate Measurement"
            "0x2A38" -> "Body Sensor Location"
            "0x2A39" -> "Heart Rate Control Point"
            "0x2A3F" -> "Alert Status"
            "0x2A40" -> "Ringer Control Point"
            "0x2A41" -> "Ringer Setting"
            "0x2A42" -> "Alert Category ID Bit Mask"
            "0x2A43" -> "Alert Category ID"
            "0x2A44" -> "Alert Notification Control Point"
            "0x2A45" -> "Unread Alert Status"
            "0x2A46" -> "New Alert"
            "0x2A47" -> "Supported New Alert Category"
            "0x2A48" -> "Supported Unread Alert Category"
            "0x2A49" -> "Blood Pressure Feature"
            "0x2A4A" -> "HID Information"
            "0x2A4B" -> "Report Map"
            "0x2A4C" -> "HID Control Point"
            "0x2A4D" -> "Report"
            "0x2A4E" -> "Protocol Mode"
            "0x2A4F" -> "Scan Interval Window"
            "0x2A50" -> "PnP ID"
            "0x2A51" -> "Glucose Feature"
            "0x2A52" -> "Record Access Control Point"
            "0x2A53" -> "RSC Measurement"
            "0x2A54" -> "RSC Feature"
            "0x2A55" -> "SC Control Point"
            "0x2A5A" -> "Aggregate"
            "0x2A5B" -> "CSC Measurement"
            "0x2A5C" -> "CSC Feature"
            "0x2A5D" -> "Sensor Location"
            "0x2A5E" -> "PLX Spot-Check Measurement"
            "0x2A5F" -> "PLX Continuous Measurement"
            "0x2A60" -> "PLX Features"
            "0x2A63" -> "Cycling Power Measurement"
            "0x2A64" -> "Cycling Power Vector"
            "0x2A65" -> "Cycling Power Feature"
            "0x2A66" -> "Cycling Power Control Point"
            "0x2A67" -> "Location and Speed"
            "0x2A68" -> "Navigation"
            "0x2A69" -> "Position Quality"
            "0x2A6A" -> "LN Feature"
            "0x2A6B" -> "LN Control Point"
            "0x2A6C" -> "Elevation"
            "0x2A6D" -> "Pressure"
            "0x2A6E" -> "Temperature"
            "0x2A6F" -> "Humidity"
            "0x2A70" -> "True Wind Speed"
            "0x2A71" -> "True Wind Direction"
            "0x2A72" -> "Apparent Wind Speed"
            "0x2A73" -> "Apparent Wind Direction"
            "0x2A74" -> "Gust Factor"
            "0x2A75" -> "Pollen Concentration"
            "0x2A76" -> "UV Index"
            "0x2A77" -> "Irradiance"
            "0x2A78" -> "Rainfall"
            "0x2A79" -> "Wind Chill"
            "0x2A7A" -> "Heat Index"
            "0x2A7B" -> "Dew Point"
            "0x2A7D" -> "Descriptor Value Changed"
            "0x2A7E" -> "Aerobic Heart Rate Lower Limit"
            "0x2A7F" -> "Aerobic Threshold"
            "0x2A80" -> "Age"
            "0x2A81" -> "Anaerobic Heart Rate Lower Limit"
            "0x2A82" -> "Anaerobic Heart Rate Upper Limit"
            "0x2A83" -> "Anaerobic Threshold"
            "0x2A84" -> "Aerobic Heart Rate Upper Limit"
            "0x2A85" -> "Date of Birth"
            "0x2A86" -> "Date of Threshold Assessment"
            "0x2A87" -> "Email Address"
            "0x2A88" -> "Fat Burn Heart Rate Lower Limit"
            "0x2A89" -> "Fat Burn Heart Rate Upper Limit"
            "0x2A8A" -> "First Name"
            "0x2A8B" -> "Five Zone Heart Rate Limits"
            "0x2A8C" -> "Gender"
            "0x2A8D" -> "Heart Rate Max"
            "0x2A8E" -> "Height"
            "0x2A8F" -> "Hip Circumference"
            "0x2A90" -> "Last Name"
            "0x2A91" -> "Maximum Recommended Heart Rate"
            "0x2A92" -> "Resting Heart Rate"
            "0x2A93" -> "Sport Type for Aerobic and Anaerobic Thresholds"
            "0x2A94" -> "Three Zone Heart Rate Limits"
            "0x2A95" -> "Two Zone Heart Rate Limits"
            "0x2A96" -> "VO2 Max"
            "0x2A97" -> "Waist Circumference"
            "0x2A98" -> "Weight"
            "0x2A99" -> "Database Change Increment"
            "0x2A9A" -> "User Index"
            "0x2A9B" -> "Body Composition Feature"
            "0x2A9C" -> "Body Composition Measurement"
            "0x2A9D" -> "Weight Measurement"
            "0x2A9E" -> "Weight Scale Feature"
            "0x2A9F" -> "User Control Point"
            "0x2AA0" -> "Magnetic Flux Density - 2D"
            "0x2AA1" -> "Magnetic Flux Density - 3D"
            "0x2AA2" -> "Language"
            "0x2AA3" -> "Barometric Pressure Trend"
            "0x2AA4" -> "Bond Management Control Point"
            "0x2AA5" -> "Bond Management Feature"
            "0x2AA6" -> "Central Address Resolution"
            "0x2AA7" -> "CGM Measurement"
            "0x2AA8" -> "CGM Feature"
            "0x2AA9" -> "CGM Status"
            "0x2AAA" -> "CGM Session Start Time"
            "0x2AAB" -> "CGM Session Run Time"
            "0x2AAC" -> "CGM Specific Ops Control Point"
            "0x2AAD" -> "Indoor Positioning Configuration"
            "0x2AAE" -> "Latitude"
            "0x2AAF" -> "Longitude"
            "0x2AB0" -> "Local North Coordinate"
            "0x2AB1" -> "Local East Coordinate"
            "0x2AB2" -> "Floor Number"
            "0x2AB3" -> "Altitude"
            "0x2AB4" -> "Uncertainty"
            "0x2AB5" -> "Location Name"
            "0x2AB6" -> "URI"
            "0x2AB7" -> "HTTP Headers"
            "0x2AB8" -> "HTTP Status Code"
            "0x2AB9" -> "HTTP Entity Body"
            "0x2ABA" -> "HTTP Control Point"
            "0x2ABB" -> "HTTPS Security"
            "0x2ABC" -> "TDS Control Point"
            "0x2ABD" -> "OTS Feature"
            "0x2ABE" -> "Object Name"
            "0x2ABF" -> "Object Type"
            "0x2AC0" -> "Object Size"
            "0x2AC1" -> "Object First -Created"
            "0x2AC2" -> "Object Last - Modified"
            "0x2AC3" -> "Object ID"
            "0x2AC4" -> "Object Properties"
            "0x2AC5" -> "Object Action Control Point"
            "0x2AC6" -> "Object List Control Point"
            "0x2AC7" -> "Object List Filter"
            "0x2AC8" -> "Object Changed"
            "0x2AC9" -> "Resolvable Private Address Only"
            "0x2ACC" -> "Fitness Machine Feature"
            "0x2ACD" -> "Treadmill Data"
            "0x2ACE" -> "Cross Trainer Data"
            "0x2ACF" -> "Step Climber Data"
            "0x2AD0" -> "Stair Climber Data"
            "0x2AD1" -> "Rower Data"
            "0x2AD2" -> "Indoor Bike Data"
            "0x2AD3" -> "Training Status"
            "0x2AD4" -> "Supported Speed Range"
            "0x2AD5" -> "Supported Inclination Range"
            "0x2AD6" -> "Supported Resistance Level Range"
            "0x2AD7" -> "Supported Heart Rate Range"
            "0x2AD8" -> "Supported Power Range"
            "0x2AD9" -> "Fitness Machine Control Point"
            "0x2ADA" -> "Fitness Machine Status"
            "0x2ADB" -> "Mesh Provisioning Data In"
            "0x2ADC" -> "Mesh Provisioning Data Out"
            "0x2ADD" -> "Mesh Proxy Data In"
            "0x2ADE" -> "Mesh Proxy Data Out"
            "0x2AE0" -> "Average Current"
            "0x2AE1" -> "Average Voltage"
            "0x2AE2" -> "Boolean"
            "0x2AE3" -> "Chromatic Distance from Planckian"
            "0x2AE4" -> "Chromaticity Coordinates"
            "0x2AE5" -> "Chromaticity in CCT and Duv Values"
            "0x2AE6" -> "Chromaticity Tolerance"
            "0x2AE7" -> "CIE 13.3 - 1995 Color Rendering Index"
            "0x2AE8" -> "Coefficient"
            "0x2AE9" -> "Correlated Color Temperature"
            "0x2AEA" -> "Count 16"
            "0x2AEB" -> "Count 24"
            "0x2AEC" -> "Country Code"
            "0x2AED" -> "Date UTC"
            "0x2AEE" -> "Electric Current"
            "0x2AEF" -> "Electric Current Range"
            "0x2AF0" -> "Electric Current Specification"
            "0x2AF1" -> "Electric Current Statistics"
            "0x2AF2" -> "Energy"
            "0x2AF3" -> "Energy in a Period of Day"
            "0x2AF4" -> "Event Statistics"
            "0x2AF5" -> "Fixed String 16"
            "0x2AF6" -> "Fixed String 24"
            "0x2AF7" -> "Fixed String 36"
            "0x2AF8" -> "Fixed String 8"
            "0x2AF9" -> "Generic Level"
            "0x2AFA" -> "Global Trade Item Number"
            "0x2AFB" -> "Illuminance"
            "0x2AFC" -> "Luminous Efficacy"
            "0x2AFD" -> "Luminous Energy"
            "0x2AFE" -> "Luminous Exposure"
            "0x2AFF" -> "Luminous Flux"
            "0x2B00" -> "Luminous Flux Range"
            "0x2B01" -> "Luminous Intensity"
            "0x2B02" -> "Mass Flow"
            "0x2B03" -> "Perceived Lightness"
            "0x2B04" -> "Percentage 8"
            "0x2B05" -> "Power"
            "0x2B06" -> "Power Specification"
            "0x2B07" -> "Relative Runtime in a Current Range"
            "0x2B08" -> "Relative Runtime in a Generic Level Range"
            "0x2B09" -> "Relative Value in a Voltage Range"
            "0x2B0A" -> "Relative Value in an Illuminance Range"
            "0x2B0B" -> "Relative Value in a Period of Day"
            "0x2B0C" -> "Relative Value in a Temperature Range"
            "0x2B0D" -> "Temperature 8"
            "0x2B0E" -> "Temperature 8 in a Period of Day"
            "0x2B0F" -> "Temperature 8 Statistics"
            "0x2B10" -> "Temperature Range"
            "0x2B11" -> "Temperature Statistics"
            "0x2B12" -> "Time Decihour 8"
            "0x2B13" -> "Time Exponential 8"
            "0x2B14" -> "Time Hour 24"
            "0x2B15" -> "Time Millisecond 24"
            "0x2B16" -> "Time Second 16"
            "0x2B17" -> "Time Second 8"
            "0x2B18" -> "Voltage"
            "0x2B19" -> "Voltage Specification"
            "0x2B1A" -> "Voltage Statistics"
            "0x2B1B" -> "Volume Flow"
            "0x2B1C" -> "Chromaticity Coordinate"
            "0x2B1D" -> "RC Feature"
            "0x2B1E" -> "RC Settings"
            "0x2B1F" -> "Reconnection Configuration Control Point"
            "0x2B20" -> "IDD Status Changed"
            "0x2B21" -> "IDD Status"
            "0x2B22" -> "IDD Annunciation Status"
            "0x2B23" -> "IDD Features"
            "0x2B24" -> "IDD Status Reader Control Point"
            "0x2B25" -> "IDD Command Control Point"
            "0x2B26" -> "IDD Command Data"
            "0x2B27" -> "IDD Record Access Control Point"
            "0x2B28" -> "IDD History Data"
            "0x2B29" -> "Client Supported Features"
            "0x2B2A" -> "Database Hash"
            "0x2B2B" -> "BSS Control Point"
            "0x2B2C" -> "BSS Response"
            "0x2B2D" -> "Emergency ID"
            "0x2B2E" -> "Emergency Text"
            "0x2B2F" -> "ACS Status"
            "0x2B30" -> "ACS Data In"
            "0x2B31" -> "ACS Data Out Notify"
            "0x2B32" -> "ACS Data Out Indicate"
            "0x2B33" -> "ACS Control Point"
            "0x2B34" -> "Enhanced Blood Pressure Measurement"
            "0x2B35" -> "Enhanced Intermediate Cuff Pressure"
            "0x2B36" -> "Blood Pressure Record"
            "0x2B37" -> "Registered User"
            "0x2B38" -> "BR - EDR Handover Data"
            "0x2B39" -> "Bluetooth SIG Data"
            "0x2B3A" -> "Server Supported Features"
            "0x2B3B" -> "Physical Activity Monitor Features"
            "0x2B3C" -> "General Activity Instantaneous Data"
            "0x2B3D" -> "General Activity Summary Data"
            "0x2B3E" -> "CardioRespiratory Activity Instantaneous Data"
            "0x2B3F" -> "CardioRespiratory Activity Summary Data"
            "0x2B40" -> "Step Counter Activity Summary Data"
            "0x2B41" -> "Sleep Activity Instantaneous Data"
            "0x2B42" -> "Sleep Activity Summary Data"
            "0x2B43" -> "Physical Activity Monitor Control Point"
            "0x2B44" -> "Activity Current Session"
            "0x2B45" -> "Physical Activity Session Descriptor"
            "0x2B46" -> "Preferred Units"
            "0x2B47" -> "High Resolution Height"
            "0x2B48" -> "Middle Name"
            "0x2B49" -> "Stride Length"
            "0x2B4A" -> "Handedness"
            "0x2B4B" -> "Device Wearing Position"
            "0x2B4C" -> "Four Zone Heart Rate Limits"
            "0x2B4D" -> "High Intensity Exercise Threshold"
            "0x2B4E" -> "Activity Goal"
            "0x2B4F" -> "Sedentary Interval Notification"
            "0x2B50" -> "Caloric Intake"
            "0x2B51" -> "TMAP Role"
            "0x2B77" -> "Audio Input State"
            "0x2B78" -> "Gain Settings Attribute"
            "0x2B79" -> "Audio Input Type"
            "0x2B7A" -> "Audio Input Status"
            "0x2B7B" -> "Audio Input Control Point"
            "0x2B7C" -> "Audio Input Description"
            "0x2B7D" -> "Volume State"
            "0x2B7E" -> "Volume Control Point"
            "0x2B7F" -> "Volume Flags"
            "0x2B80" -> "Volume Offset State"
            "0x2B81" -> "Audio Location"
            "0x2B82" -> "Volume Offset Control Point"
            "0x2B83" -> "Audio Output Description"
            "0x2B84" -> "Set Identity Resolving Key"
            "0x2B85" -> "Coordinated Set Size"
            "0x2B86" -> "Set Member Lock"
            "0x2B87" -> "Set Member Rank"
            "0x2B88" -> "Encrypted Data Key Material"
            "0x2B89" -> "Apparent Energy 32"
            "0x2B8A" -> "Apparent Power"
            "0x2B8B" -> "Live Health Observations"
            "0x2B8C" -> "CO \\{} text-subscript { 2 } Concentration"
            "0x2B8D" -> "Cosine of the Angle"
            "0x2B8E" -> "Device Time Feature"
            "0x2B8F" -> "Device Time Parameters"
            "0x2B90" -> "Device Time"
            "0x2B91" -> "Device Time Control Point"
            "0x2B92" -> "Time Change Log Data"
            "0x2B93" -> "Media Player Name"
            "0x2B94" -> "Media Player Icon Object ID"
            "0x2B95" -> "Media Player Icon URL"
            "0x2B96" -> "Track Changed"
            "0x2B97" -> "Track Title"
            "0x2B98" -> "Track Duration"
            "0x2B99" -> "Track Position"
            "0x2B9A" -> "Playback Speed"
            "0x2B9B" -> "Seeking Speed"
            "0x2B9C" -> "Current Track Segments Object ID"
            "0x2B9D" -> "Current Track Object ID"
            "0x2B9E" -> "Next Track Object ID"
            "0x2B9F" -> "Parent Group Object ID"
            "0x2BA0" -> "Current Group Object ID"
            "0x2BA1" -> "Playing Order"
            "0x2BA2" -> "Playing Orders Supported"
            "0x2BA3" -> "Media State"
            "0x2BA4" -> "Media Control Point"
            "0x2BA5" -> "Media Control Point Opcodes Supported"
            "0x2BA6" -> "Search Results Object ID"
            "0x2BA7" -> "Search Control Point"
            "0x2BA8" -> "Energy 32"
            "0x2BA9" -> "Media Player Icon Object Type"
            "0x2BAA" -> "Track Segments Object Type"
            "0x2BAB" -> "Track Object Type"
            "0x2BAC" -> "Group Object Type"
            "0x2BAD" -> "Constant Tone Extension Enable"
            "0x2BAE" -> "Advertising Constant Tone Extension Minimum Length"
            "0x2BAF" -> "Advertising Constant Tone Extension Minimum Transmit Count"
            "0x2BB0" -> "Advertising Constant Tone Extension Transmit Duration"
            "0x2BB1" -> "Advertising Constant Tone Extension Interval"
            "0x2BB2" -> "Advertising Constant Tone Extension PHY"
            "0x2BB3" -> "Bearer Provider Name"
            "0x2BB4" -> "Bearer UCI"
            "0x2BB5" -> "Bearer Technology"
            "0x2BB6" -> "Bearer URI Schemes Supported List"
            "0x2BB7" -> "Bearer Signal Strength"
            "0x2BB8" -> "Bearer Signal Strength Reporting Interval"
            "0x2BB9" -> "Bearer List Current Calls"
            "0x2BBA" -> "Content Control ID"
            "0x2BBB" -> "Status Flags"
            "0x2BBC" -> "Incoming Call Target Bearer URI"
            "0x2BBD" -> "Call State"
            "0x2BBE" -> "Call Control Point"
            "0x2BBF" -> "Call Control Point Optional Opcodes"
            "0x2BC0" -> "Termination Reason"
            "0x2BC1" -> "Incoming Call"
            "0x2BC2" -> "Call Friendly Name"
            "0x2BC3" -> "Mute"
            "0x2BC4" -> "Sink ASE"
            "0x2BC5" -> "Source ASE"
            "0x2BC6" -> "ASE Control Point"
            "0x2BC7" -> "Broadcast Audio Scan Control Point"
            "0x2BC8" -> "Broadcast Receive State"
            "0x2BC9" -> "Sink PAC"
            "0x2BCA" -> "Sink Audio Locations"
            "0x2BCB" -> "Source PAC"
            "0x2BCC" -> "Source Audio Locations"
            "0x2BCD" -> "Available Audio Contexts"
            "0x2BCE" -> "Supported Audio Contexts"
            "0x2BCF" -> "Ammonia Concentration"
            "0x2BD0" -> "Carbon Monoxide Concentration"
            "0x2BD1" -> "Methane Concentration"
            "0x2BD2" -> "Nitrogen Dioxide Concentration"
            "0x2BD3" -> "Non -Methane Volatile Organic Compounds Concentration"
            "0x2BD4" -> "Ozone Concentration"
            "0x2BD5" -> "Particulate Matter - PM1 Concentration"
            "0x2BD6" -> "Particulate Matter - PM2.5 Concentration"
            "0x2BD7" -> "Particulate Matter - PM10 Concentration"
            "0x2BD8" -> "Sulfur Dioxide Concentration"
            "0x2BD9" -> "Sulfur Hexafluoride Concentration"
            "0x2BDA" -> "Hearing Aid Features"
            "0x2BDB" -> "Hearing Aid Preset Control Point"
            "0x2BDC" -> "Active Preset Index"
            "0x2BDD" -> "Stored Health Observations"
            "0x2BDE" -> "Fixed String 64"
            "0x2BDF" -> "High Temperature"
            "0x2BE0" -> "High Voltage"
            "0x2BE1" -> "Light Distribution"
            "0x2BE2" -> "Light Output"
            "0x2BE3" -> "Light Source Type"
            "0x2BE4" -> "Noise"
            "0x2BE5" -> "Relative Runtime in a Correlated Color Temperature Range"
            "0x2BE6" -> "Time Second 32"
            "0x2BE7" -> "VOC Concentration"
            "0x2BE8" -> "Voltage Frequency"
            "0x2BE9" -> "Battery Critical Status"
            "0x2BEA" -> "Battery Health Status"
            "0x2BEB" -> "Battery Health Information"
            "0x2BEC" -> "Battery Information"
            "0x2BED" -> "Battery Level Status"
            "0x2BEE" -> "Battery Time Status"
            "0x2BEF" -> "Estimated Service Date"
            "0x2BF0" -> "Battery Energy Status"
            "0x2BF1" -> "Observation Schedule Changed"
            "0x2BF2" -> "Current Elapsed Time"
            "0x2BF3" -> "Health Sensor Features"
            "0x2BF4" -> "GHS Control Point"
            "0x2BF5" -> "LE GATT Security Levels"
            "0x2BF6" -> "ESL Address"
            "0x2BF7" -> "AP Sync Key Material"
            "0x2BF8" -> "ESL Response Key Material"
            "0x2BF9" -> "ESL Current Absolute Time"
            "0x2BFA" -> "ESL Display Information"
            "0x2BFB" -> "ESL Image Information"
            "0x2BFC" -> "ESL Sensor Information"
            "0x2BFD" -> "ESL LED Information"
            "0x2BFE" -> "ESL Control Point"
            "0x2BFF" -> "UDI for Medical Devices"
            else -> "Unknown Characteristics"
        }

    /**
     * 获取属性
     */
    fun getProperties(property: Int): List<String> {
        val properties: MutableList<String> = ArrayList()
        for (i in 0..7) {
            when (property and (1 shl i)) {
                0x01 -> properties.add("Broadcast")
                0x02 -> properties.add("Read")
                0x04 -> properties.add("Write No Response")
                0x08 -> properties.add("Write")
                0x10 -> properties.add("Notify")
                0x20 -> properties.add("Indicate")
                0x40 -> properties.add("Authenticated Signed Writes")
                0x80 -> properties.add("Extended Properties")
            }
        }
        return properties
    }

    fun getShortUUID(uuid: UUID) =
        "0x${uuid.toString().substring(4, 8).uppercase(Locale.getDefault())}"
}