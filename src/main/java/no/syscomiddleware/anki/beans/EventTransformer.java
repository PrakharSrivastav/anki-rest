package no.syscomiddleware.anki.beans;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventTransformer {
    // ElasticSearch accepts Object of type IndexRequest, this section transforms the JSONObject to IndexRequest based on type
    public IndexRequest map(final JSONObject model) throws JSONException {
        final IndexRequest request = new IndexRequest("prakhar", "final-reading");

        final String type = model.getString("type");
        if (type != null && !type.isEmpty()) {
            // check type and convert to proper indexrequest
            switch (type) {
                case "CAR_TRANSITIONED":
                    request.source(this.carTransitionedEvent(model), XContentType.JSON);
                    break;
                case "VEHICLE_DELOCALIZED":
                    request.source(this.vehicleDelocalizedEvent(model), XContentType.JSON);
                    break;
                case "LANE_CHANGED":
                    request.source(this.laneChangedEvent(model), XContentType.JSON);
                    break;
                case "SPEED_MEASUREMENT":
                    request.source(this.speedMeasuredEvent(model), XContentType.JSON);
                    break;
                case "LAP_COMPLETED":
                    request.source(this.lapCompletedEvent(model), XContentType.JSON);
                    break;
            }
            return request;
        }
        return null;
    }


    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy HH:mm:ss:SSSSSS")
            .withLocale(Locale.ROOT)
            .withChronology(ISOChronology.getInstanceUTC());

    private final DateTimeFormatter formatterTimestamp = DateTimeFormat.forPattern("dd/MM/yy HH:mm:ss")
            .withLocale(Locale.ROOT)
            .withChronology(ISOChronology.getInstanceUTC());

    private Map<String, Object> carTransitionedEvent(final JSONObject model) throws JSONException {
        //  {'type':'CAR_TRANSITIONED',         'carId':'FC:70:98:68:10:BA:01',         'deviceId':'',         'carName':'Skull',         'trackSegment':22,         'lap':123,         'raceStatus':'',         'trackStyle':'Left Turn',         'raceId':1,         'dateTime':1537366468,         'dateTimeString':'18/09/19 16:14:28:403930',         'demozone':''}
        final Map<String, Object> map = new HashMap<>();
        if (model.has("type")) { map.put("type", model.getString("type")); }
        if (model.has("carId")) { map.put("carId", model.getString("carId")); }
        if (model.has("deviceId")) { map.put("deviceId", model.getString("deviceId")); }
        if (model.has("raceId")) { map.put("raceId", model.getInt("raceId")); }
        if (model.has("carName")) { map.put("carName", model.getString("carName")); }
        if (model.has("trackSegment")) { map.put("trackSegment", model.getInt("trackSegment")); }
        if (model.has("lap")) { map.put("lap", model.getInt("lap")); }
        if (model.has("raceStatus")) { map.put("raceStatus", model.getString("raceStatus")); }
        if (model.has("trackStyle")) { map.put("trackStyle", model.getString("trackStyle")); }
        if (model.has("dateTime")) { map.put("dateTime", this.getDateTime(model)); }
        if (model.has("dateTimeString")) { map.put("preciseDateTime", this.getDateTimeString(model)); }
        if (model.has("demozone")) { map.put("demozone", model.getString("demozone")); }
        return map;
    }

    private Map<String, Object> vehicleDelocalizedEvent(final JSONObject model) throws JSONException {
        //  {'type':'VEHICLE_DELOCALIZED','carId': 'FC:70:98:68:10:BA:01','deviceId': '','carName': 'Skull','lastKnownTrack': 2,'raceStatus': '','raceId': 1,'lap': 123,'dateTime': 1537366466,'dateTimeString': '18/09/19 16:14:26:396295','demozone': ''}
        final Map<String, Object> map = new HashMap<>();
        if (model.has("type")) { map.put("type", model.getString("type")); }
        if (model.has("carId")) { map.put("carId", model.getString("carId")); }
        if (model.has("deviceId")) { map.put("deviceId", model.getString("deviceId")); }
        if (model.has("carName")) { map.put("carName", model.getString("carName")); }
        if (model.has("lastKnownTrack")) { map.put("lastKnownTrack", model.getInt("lastKnownTrack")); }
        if (model.has("raceStatus")) { map.put("raceStatus", model.getString("raceStatus")); }
        if (model.has("raceId")) { map.put("raceId", model.getInt("raceId")); }
        if (model.has("lap")) { map.put("lap", model.getInt("lap")); }
        if (model.has("dateTime")) { map.put("dateTime", this.getDateTime(model)); }
        if (model.has("dateTimeString")) { map.put("preciseDateTime", this.getDateTimeString(model)); }
        if (model.has("demozone")) { map.put("demozone", model.getString("demozone")); }
        return map;
    }

    private Map<String, Object> laneChangedEvent(final JSONObject model) throws JSONException {
        //{'type':'LANE_CHANGED', 'carId': 'FC:70:98:68:10:BA:01', 'lapTime': 0, 'deviceId': '', 'carName': 'Skull', 'lap': 123, 'raceStatus': '', 'raceId': 1, 'dateTime': 1537366465, 'dateTimeString': '18/09/19 16:14:25:390125', 'demozone': ''}
        final Map<String, Object> map = new HashMap<>();
        if (model.has("type")) { map.put("type", model.getString("type")); }
        if (model.has("carId")) { map.put("carId", model.getString("carId")); }
        if (model.has("deviceId")) { map.put("deviceId", model.getString("deviceId")); }
        if (model.has("raceId")) { map.put("raceId", model.getInt("raceId")); }
        if (model.has("carName")) { map.put("carName", model.getString("carName")); }
        if (model.has("raceStatus")) { map.put("raceStatus", model.getString("raceStatus")); }
        if (model.has("dateTime")) { map.put("dateTime", this.getDateTime(model)); }
        if (model.has("dateTimeString")) { map.put("preciseDateTime", this.getDateTimeString(model)); }
        if (model.has("demozone")) { map.put("demozone", model.getString("demozone")); }
        if (model.has("lap")) { map.put("lap", model.getInt("lap")); }
        if (model.has("lapTime")) { map.put("lapTime", model.getInt("lapTime")); }
        return map;
    }

    private Map<String, Object> speedMeasuredEvent(final JSONObject model) throws JSONException {
        //{'type':'SPEED_MEASUREMENT', 'carId': 'FC:70:98:68:10:BA:01', 'deviceId': '', 'carName': 'Skull', 'lap': 123, 'raceStatus': '', 'raceId': 1, 'trackId': 33, 'dateTime': 1537366461, 'dateTimeString': '18/09/19 16:14:21', 'speed': 5765, 'demozone': ''}
        final Map<String, Object> map = new HashMap<>();
        if (model.has("type")) { map.put("type", model.getString("type")); }
        if (model.has("carId")) { map.put("carId", model.getString("carId")); }
        if (model.has("deviceId")) { map.put("deviceId", model.getString("deviceId")); }
        if (model.has("trackId")) { map.put("trackId", model.getString("trackId")); }
        if (model.has("raceId")) { map.put("raceId", model.getInt("raceId")); }
        if (model.has("carName")) { map.put("carName", model.getString("carName")); }
        if (model.has("raceStatus")) { map.put("raceStatus", model.getString("raceStatus")); }
        if (model.has("dateTime")) { map.put("dateTime", this.getDateTime(model)); }
//        if (model.has("dateTimeString")) { map.put("preciseDateTime", this.getDateTimeString(model)); }
        if (model.has("demozone")) { map.put("demozone", model.getString("demozone")); }
        if (model.has("lap")) { map.put("lap", model.getInt("lap")); }
        if (model.has("speed")) { map.put("speed", model.getInt("speed")); }
        return map;
    }

    private Map<String, Object> lapCompletedEvent(final JSONObject model) throws JSONException {
        //{'type':'LAP_COMPLETED', 'lapTime': 17077, 'raceId': 1, 'raceStatus': '', 'deviceId': '', 'dateTimeString': '18/09/19 16:14:18', 'carName': 'Skull', 'carId': 'FC:70:98:68:10:BA:01', 'dateTime': 1537366458, 'lap': 123, 'demozone': ''}
        final Map<String, Object> map = new HashMap<>();
        if (model.has("type")) { map.put("type", model.getString("type")); }
        if (model.has("carId")) { map.put("carId", model.getString("carId")); }
        if (model.has("deviceId")) { map.put("deviceId", model.getString("deviceId")); }
        if (model.has("raceId")) { map.put("raceId", model.getInt("raceId")); }
        if (model.has("carName")) { map.put("carName", model.getString("carName")); }
        if (model.has("raceStatus")) { map.put("raceStatus", model.getString("raceStatus")); }
        if (model.has("dateTime")) { map.put("dateTime", this.getDateTime(model)); }
//        if (model.has("dateTimeString")) { map.put("preciseDateTime", this.getDateTimeString(model)); }
        if (model.has("demozone")) { map.put("demozone", model.getString("demozone")); }
        if (model.has("lap")) { map.put("lap", model.getInt("lap")); }
        if (model.has("lapTime")) { map.put("lapTime", model.getInt("lapTime")); }
        return map;
    }

    private DateTime getDateTime(JSONObject model) throws JSONException {
        final Date date = new Date(model.getInt("dateTime") * 1000l);
        final SimpleDateFormat jdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return this.formatterTimestamp.parseDateTime(jdf.format(date));
    }

    private DateTime getDateTimeString(JSONObject model) throws JSONException {
        return this.formatter.parseDateTime(model.getString("dateTimeString"));
    }
}