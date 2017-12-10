1. In Windows, open Command Prompt (cmd.exe)

2. Change to folder {ndcscanrecorder}/bin

3. Run command:	java -classpath . com.plaslantic.common.NdcScanRecorder



CONFIG:

Located at /bin/config/app.properties (NOTE the 'bin' folder!)

ipaddress=192.168.0.88  				# Machine to connect to
port=20000								# Scanner connect port
scanner_name=Welex1      				# Prefixed to scan data CSV files
cycle_seconds_override=-1 				# Use it to set a shorter poll period (set to -1 to use default=1740 secs). Useful for debugging/testing.
data_out_folder=C:\\folder1\\scan_data  # where CSV files go


COMMAND LINE CONFIG

scanner_name, ipaddress & data_out_folder can be overridden by specifying 
-D argument:

java -Dscanner_name=Welex1 -Dipaddress=192.168.0.88 -classpath . com.plaslantic.common.NdcScanRecorder

(if command line arg not supplied, app will fall back to config/app.properties values)



For questions/issues, email Gert<gvanto@gmail.com>
