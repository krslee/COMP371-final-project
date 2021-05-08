package com.example.finalproject;

import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class FragmentGenerate extends Fragment {
    private ImageView color1;
    private ImageView color2;
    private ImageView color3;
    private ImageView color4;
    private ImageView color5;

    private Button button_random;
    private Button button_image;
    private Button button_scratch;
    private Button button_save;

    private static final String api_url="http://colormind.io/api/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    private FirebaseDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate, container, false);

        color1 = view.findViewById(R.id.color1_generate);
        color2 = view.findViewById(R.id.color2_generate);
        color3 = view.findViewById(R.id.color3_generate);
        color4 = view.findViewById(R.id.color4_generate);
        color5 = view.findViewById(R.id.color5_generate);
        button_random = view.findViewById(R.id.button_random);
        button_scratch = view.findViewById(R.id.button_scratch);
        button_image = view.findViewById(R.id.button_image);
        button_save = view.findViewById(R.id.button_generate_save);

        database = FirebaseDatabase.getInstance();

        button_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePalette();
            }
        });

        button_scratch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScratchActivity.class);
                startActivity(intent);
            }
        });

        button_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                startActivity(intent);
            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                ArrayList<Integer> color1RGB = getRGBValues(color1);
                ArrayList<Integer> color2RGB = getRGBValues(color2);
                ArrayList<Integer> color3RGB = getRGBValues(color3);
                ArrayList<Integer> color4RGB = getRGBValues(color4);
                ArrayList<Integer> color5RGB = getRGBValues(color5);

                Intent intent = new Intent(getActivity(), NameActivity.class);
                intent.putExtra("color1RGB", color1RGB);
                intent.putExtra("color2RGB", color2RGB);
                intent.putExtra("color3RGB", color3RGB);
                intent.putExtra("color4RGB", color4RGB);
                intent.putExtra("color5RGB", color5RGB);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generatePalette();
    }

    public void generatePalette() {
        // add header to client
        client.addHeader("Accept", "application/json");
        RequestParams params = new RequestParams();
        params.put("model", "default");

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("model", "default");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(getContext(), api_url, entity, "application/json", new AsyncHttpResponseHandler() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("api response", new String(responseBody));

                int width = color1.getWidth();
                int height = color1.getHeight();
                Log.d("info", Integer.toString(height));
                int sectionWidth = width / 5;

                ArrayList<ImageView> views = new ArrayList<>();
                views.add(color1);
                views.add(color2);
                views.add(color3);
                views.add(color4);
                views.add(color5);

                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    JSONArray result = json.getJSONArray("result");

                    // Log.i("values", result.get(0).toString());

                    for (int i = 0; i < result.length(); i++) {
                        JSONArray values = result.getJSONArray(i);
                        int r = values.getInt(0);
                        int g = values.getInt(1);
                        int b = values.getInt(2);
                        Paint paint = new Paint();
                        paint.setColor(Color.rgb(r, g, b));

                        views.get(i).setBackgroundColor(Color.rgb(r, g, b));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // create random palette to set as the default
                Random rnd = new Random();
                // get two random colors and set as first and last colors
                int c1 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                int c5 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                // get colors in between two end colors
                int c2 = (Integer) new ArgbEvaluator().evaluate((float)1/4, c1, c5);
                int c3 = (Integer) new ArgbEvaluator().evaluate((float)2/4, c1, c5);
                int c4 = (Integer) new ArgbEvaluator().evaluate((float)3/4, c1, c5);

                // set color imageViews accordingly
                color1.setBackgroundColor(c1);
                color2.setBackgroundColor(c2);
                color3.setBackgroundColor(c3);
                color4.setBackgroundColor(c4);
                color5.setBackgroundColor(c5);
            }
        });

    }

    // method to get rgb values
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Integer> getRGBValues(ImageView view) {
        // get color of color imageView
        ColorDrawable drawable = (ColorDrawable) view.getBackground();
        int color = drawable.getColor();

        // get rgb values of the color
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        // add to arrayList and return arrayList
        ArrayList<Integer> rgbValues = new ArrayList<>();
        rgbValues.add(red);
        rgbValues.add(green);
        rgbValues.add(blue);

        return rgbValues;
    }
}
