import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.json.*;

public class JsonReader {

	public static String createFile(
			String uuidInput,
			String startInput, 
			String endInput, 
			String serverInput, 
			String timeZoneInput, 
			String keyInput, 
			String fileNameInput
	){
		int interval = 300;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		TimeZone.setDefault(TimeZone.getTimeZone(timeZoneInput));
		dateFormat.setTimeZone(TimeZone.getDefault());
		File file = new File(fileNameInput);
		
		if(dateFormat.parse(startInput, new ParsePosition(0)) != null){
			startInput = Long.toString(dateFormat.parse(startInput,  new ParsePosition(0)).getTime()/1000);
		}
		if(dateFormat.parse(endInput, new ParsePosition(0)) != null){
			endInput = Long.toString(dateFormat.parse(endInput,  new ParsePosition(0)).getTime()/1000);
		}
	
		JSONObject json;
		try {
			json = readJsonFromUrl("https://" + serverInput + ".foobot.io/v2/device/" + uuidInput + "/datapoint/" + startInput + "/" + endInput + "/" + interval + "/?api_key=" + keyInput);
		} catch (JSONException e){
			return "Server reading - JSONException";
		} catch (IOException e){
			return "Server reading - IOException";
		}

		@SuppressWarnings("unused")
		String uuid = json.getString("uuid");
		int start = json.getInt("start");
		@SuppressWarnings("unused")
		int end = json.getInt("end");

		JSONArray sensors = json.getJSONArray("sensors");
		JSONArray units = json.getJSONArray("units");
		JSONArray datapoints = json.getJSONArray("datapoints");
		JSONArray datapoint;
		int numberOfSensors = sensors.length();
		long expectedTime = start;
		
		if(!file.exists()){
			try {
				file.createNewFile();
				FileWriter fileWriter = new FileWriter(file);
	
				//get time title
				fileWriter.append(sensors.getString(0) + " (" + units.getString(0) + "),");
				//append date title
				fileWriter.append("Device Local Time,");
				//get the rest of the sensors
				for (int i = 1; i < numberOfSensors; i++){
					fileWriter.append(sensors.getString(i) + " (" + units.getString(i) + "),");
				}
				fileWriter.append("\n");
	
				//get data
				for (int i = 0; i < datapoints.length(); i++){
					
					//get new line
					datapoint = datapoints.optJSONArray(i);
					
					//Check for missing entries
					while (datapoint.getLong(0) != expectedTime){
						fileWriter.append(expectedTime + ",");
						fileWriter.append(dateFormat.format(expectedTime*1000) + "\n");
						expectedTime += interval;
					}
					
					//get time
					fileWriter.append(datapoint.getLong(0) + ",");
					//append date
					fileWriter.append(dateFormat.format(datapoint.getLong(0)*1000) + ",");
					//get the rest of values
					for (int j = 1; j < numberOfSensors; j++){
						fileWriter.append(datapoint.getDouble(j) + ",");
					}
					
					fileWriter.append("\n");
					expectedTime += interval;
				}
				fileWriter.close();
			} catch (IOException e){
				return "IOException occured";
			}

		} else {
			return "Such file already exists";
		}
		return "Success!";
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException{
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
}
