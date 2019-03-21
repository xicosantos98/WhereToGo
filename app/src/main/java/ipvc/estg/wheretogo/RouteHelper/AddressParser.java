package ipvc.estg.wheretogo.RouteHelper;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddressParser {

    public HashMap<String, JSONObject> getInfo(JSONObject jObject) {

        JSONArray results;
        HashMap<String, JSONObject> hm = new HashMap<>();

        try {
            results = jObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                JSONObject objectResult = results.getJSONObject(i);
                String addres = objectResult.getString("formatted_address");

                JSONObject objectLocation = objectResult.getJSONObject("geometry").getJSONObject("location");
                hm.put(addres, objectLocation);
                Log.d("JSON", "CHEGUEI");
            }

        } catch (JSONException ex) {
            Log.d("ERRO JSON", ex.getMessage());
        }
        return hm;

    }


}
