package sksa.aa.tweaker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Map;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences mysharedpreferences = getPreferences(MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Button toapp = findViewById(R.id.toapp_button);
        toapp.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, AppsList.class);
                        startActivity(intent);
                    }
                }
        );

        Button rebootbutton = findViewById(R.id.reboot_button);
        final DialogFragment rebootDialog = new RebootDialog();
        rebootbutton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {
                        rebootDialog.show(getSupportFragmentManager(), "RebootDialog");
                    }
                }
        );

        final Button rebootButton = findViewById(R.id.reboot_button);
        final Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(600);
        anim.setStartOffset(0);
        anim.setFillAfter(true);
        rebootButton.setAnimation(anim);

        final Boolean[] animationRun = {false};

        final Button nospeed = findViewById(R.id.nospeed);
        final ImageView nospeedimg = findViewById(R.id.speedhackstatus);
        if(load("aa_speed_hack")) {
            save(true, "aa_speed_hack");
            nospeed.setText("Enable " + getText(R.string.unlimited_scrolling_when_driving));
            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            nospeedimg.setColorFilter(Color.argb(255,0,255,0));
        } else {
            save(false, "aa_speed_hack");
            nospeed.setText("Disable " + getText(R.string.unlimited_scrolling_when_driving));
            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            nospeedimg.setColorFilter(Color.argb(255,255,0,0));
        }

        nospeed.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_speed_hack")){
                            revert("nospeed");
                            nospeed.setText("Enable " + getText(R.string.unlimited_scrolling_when_driving));
                            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            nospeedimg.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforspeed(view, nospeed);
                            nospeedimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            nospeedimg.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button assistshort = findViewById(R.id.assistshort);
        final ImageView assisthackimg = findViewById(R.id.shortcutstatus);
        if(load("assist_short")) {
            save (true, "assist_short");
            assistshort.setText("Disable " + getText(R.string.enable_assistant_shortcuts));
            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            assisthackimg.setColorFilter(Color.argb(255,0,255,0));
        } else {
            save (false, "assist_short");
            assistshort.setText("Enable " + getText(R.string.enable_assistant_shortcuts));
            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            assisthackimg.setColorFilter(Color.argb(255,255,0,0));
        }


        assistshort.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("assist_short")){
                            revert("assist_short");
                            assistshort.setText("Disable " + getText(R.string.enable_assistant_shortcuts));
                            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            assisthackimg.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforassistshort(view, assistshort);
                            assisthackimg.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            assisthackimg.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button taplimitat = findViewById(R.id.taplimit);
        final ImageView taplimitstatus = findViewById(R.id.sixtapstatus);
        if(load("aa_six_tap")) {
            save (true, "aa_six_tap");
            taplimitat.setText("Enable " + getText(R.string.disable_speed_limitations));
            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            taplimitstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            save (false, "aa_six_tap");
            taplimitat.setText("Disable " + getText(R.string.disable_speed_limitations));
            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            taplimitstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        taplimitat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_six_tap")){
                            revert("aa_six_tap");
                            taplimitat.setText("Enable " + getText(R.string.disable_speed_limitations));
                            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            taplimitstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchfortouchlimit(view, taplimitat);
                            taplimitstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            taplimitstatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button startupnav = findViewById(R.id.startup);
        final ImageView navstatus = findViewById(R.id.navstatus);
        if(load("aa_startup_policy")) {
            save (true, "aa_startup_policy");
            startupnav.setText("Enable " + getText(R.string.navigation_at_start));
            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            navstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            save (false, "aa_startup_policy");
            startupnav.setText("Disable " + getText(R.string.navigation_at_start));
            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            navstatus.setColorFilter(Color.argb(255,255,0,0));
        }
        startupnav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_startup_policy")){
                            revert("aa_startup_policy");
                            startupnav.setText("Enable " + getText(R.string.navigation_at_start));
                            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            navstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            navpatch(view, startupnav);
                            navstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            navstatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button patchapps = findViewById(R.id.patchapps);
        final ImageView patchappstatus = findViewById(R.id.patchedappstatus);

        if(load("aa_patched_apps") || load("after_delete")) {
            save(true, "aa_patched_apps");
            patchapps.setText("Unpatch " + getText(R.string.patch_custom_apps));
            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            patchappstatus.setColorFilter(Color.argb(255,0,255,0));
        } else {
            save(false, "aa_patched_apps");
            patchapps.setText("Patch " + getText(R.string.patch_custom_apps));
            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            patchappstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        patchapps.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_patched_apps")){
                            revert("aa_patched_apps");
                            patchapps.setText("Patch " + getText(R.string.patch_custom_apps));
                            patchappstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            patchappstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchforapps(view, patchappstatus);
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button assistanim = findViewById(R.id.assistanim);
        final ImageView assistanimstatus = findViewById(R.id.assistanimstatus);
        if(load("aa_assistant_rail")) {
            save (true, "aa_assistant_rail");
            assistanim.setText("Disable " + getText(R.string.enable_assistant_animation_in_navbar));
            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            assistanimstatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            save (false, "aa_assistant_rail");
            assistanim.setText("Enable " + getText(R.string.enable_assistant_animation_in_navbar));
            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            assistanimstatus.setColorFilter(Color.argb(255,255,0,0));
        }

        assistanim.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_assistant_rail")){
                            revert("aa_assistant_rail");
                            assistanim.setText("Enable " + getText(R.string.enable_assistant_animation_in_navbar));
                            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            assistanimstatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            patchrailassistant(view, assistanim);
                            assistanimstatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            assistanimstatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });

        final Button batteryoutline = findViewById(R.id.battoutline);
        final ImageView batterystatus = findViewById(R.id.batterystatus);
        if(load("aa_battery_outline")) {
            save(true, "aa_battery_outline");
            batteryoutline.setText("Enable " + getText(R.string.battery_outline_string));
            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
            batterystatus.setColorFilter(Color.argb(255,0,255,0));

        } else {
            save (false, "aa_battery_outline");
            batteryoutline.setText("Disable " + getText(R.string.battery_outline_string));
            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
            batterystatus.setColorFilter(Color.argb(255,255,0,0));
        }

        batteryoutline.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_battery_outline")){
                            revert("aa_battery_outline");
                            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                            batterystatus.setColorFilter(Color.argb(255,255,0,0));
                            if(!animationRun[0]) {
                                rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                        else {
                            battOutline(view, batteryoutline);
                            batterystatus.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                            batterystatus.setColorFilter(Color.argb(255,255,255,0));
                            if(!animationRun[0]) {
rebootButton.setVisibility(View.VISIBLE);
                                anim.start();
                                animationRun[0] = true;
                            }
                        }
                    }
                });
    }



    private void revert(String toRevert) {

        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        switch (toRevert) {
            case "nospeed":

                new Thread() {
                    @Override
                    public void run() {
                        String path = getApplicationInfo().dataDir;
                        boolean suitableMethodFound = true;
                        copyAssets();

                        appendText(logs, "\n\n-- Reverting speed hack  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DROP TRIGGER IF EXISTS aa_speed_hack;'"
                        ).getStreamLogsWithLabels());
                        save(false, "aa_speed_hack");
                    }


                }.start();
                break;

            case "assist_short":

                new Thread() {
                    @Override
                    public void run() {
                        String path = getApplicationInfo().dataDir;
                        boolean suitableMethodFound = true;
                        copyAssets();

                        appendText(logs, "\n\n-- Reverting assistant shortcut hack  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DROP TRIGGER IF EXISTS ASSIST_SHORT;'"
                        ).getStreamLogsWithLabels());
                        save(false, "assist_short");
                    }


                }.start();
                break;

            case "aa_patched_apps":

                new Thread() {
                    @Override
                    public void run() {
                        String path = getApplicationInfo().dataDir;
                        boolean suitableMethodFound = true;
                        copyAssets();

                        appendText(logs, "\n\n-- Reverting patched apps hack  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DROP TRIGGER IF EXISTS aa_patched_apps;'"
                        ).getStreamLogsWithLabels());
                        save(false, "aa_patched_apps");
                    }


                }.start();
                break;

            case "aa_six_tap":

                new Thread() {
                    @Override
                    public void run() {
                        String path = getApplicationInfo().dataDir;
                        boolean suitableMethodFound = true;
                        copyAssets();

                        appendText(logs, "\n\n-- Reverting six tap limit hack  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DROP TRIGGER IF EXISTS aa_six_tap;'"
                        ).getStreamLogsWithLabels());
                        save(false, "aa_six_tap");
                    }


                }.start();
                break;

            case "aa_assistant_rail":

                new Thread() {
                    @Override
                    public void run() {
                        String path = getApplicationInfo().dataDir;
                        boolean suitableMethodFound = true;
                        copyAssets();

                        appendText(logs, "\n\n-- Reverting assistant in navbar hack  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DROP TRIGGER IF EXISTS aa_assistant_rail;'"
                        ).getStreamLogsWithLabels());
                        save(false, "aa_assistant_rail");
                    }


                }.start();
                break;

            case "aa_startup_policy":
                new Thread() {
                    @Override
                    public void run() {
                        String path = getApplicationInfo().dataDir;
                        boolean suitableMethodFound = true;
                        copyAssets();

                        appendText(logs, "\n\n-- Reverting assistant in navbar hack  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DROP TRIGGER IF EXISTS aa_startup_policy;'"
                        ).getStreamLogsWithLabels());
                        save(false, "aa_startup_policy");
                    }


                }.start();
                break;

            case "aa_battery_outline":
                new Thread() {
                    @Override
                    public void run() {
                        String path = getApplicationInfo().dataDir;
                        boolean suitableMethodFound = true;
                        copyAssets();
                        appendText(logs, "\n\n-- Reverting assistant in navbar hack  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DROP TRIGGER IF EXISTS aa_battery_outline;'"
                        ).getStreamLogsWithLabels());
                        save(false, "aa_battery_outline");
                    }


                }.start();
                break;
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                TextView textView = findViewById(R.id.logs);
                ClipData clip = ClipData.newPlainText("logs", textView.getText());
                clipboard.setPrimaryClip(clip);
            break;

            case R.id.about:
                DialogFragment aboutDialog = new AboutDialog();
                aboutDialog.show(getSupportFragmentManager(), "AboutDialog");

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void save(final boolean isChecked, String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

     public boolean load(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }



    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu, menu );
        return true;
    }


    public void patchforapps(final View view, final ImageView textView) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        SharedPreferences appsListPref = getApplicationContext().getSharedPreferences("appsListPref", 0);
        Map<String, ?> allEntries = appsListPref.getAll();
        if (allEntries.isEmpty()) {
            Intent intent = new Intent(this, AppsList.class);
            this.startActivity(intent);
            Toast.makeText(getApplicationContext(), "Choose apps to whitelist.", Toast.LENGTH_LONG).show();
        } else {
            logs.append("--  Apps which will be added to whitelist: --\n");
            String whiteListString = "";
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                logs.append("\t\t- " + entry.getValue() + " (" + entry.getKey() + ")\n");
                whiteListString += "," + entry.getKey();
            }

            whiteListString = whiteListString.replaceFirst(",", "");
            final String whiteListStringFinal = whiteListString;

            new Thread() {
                @Override
                public void run() {
                    String path = getApplicationInfo().dataDir;
                    boolean suitableMethodFound = true;
                    copyAssets();

                    appendText(logs, "\n\n-- Drop Triggers  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS aa_patched_apps;'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, "\n\n--  DELETE old Flags  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"app_white_list\";'"
                    ).getStreamLogsWithLabels());

                    if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #1  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER after_delete AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, "\n--  end SQL method #1  --");

                    } else if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM Packages WHERE packageName=\"com.google.android.gms.car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #2  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", (SELECT version FROM Packages WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 230, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car_setup\", 234, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method #2  --");

                    } else if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car#car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #3  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER after_delete AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car#car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car#car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method #3  --");

                    } else if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"'").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method #4  --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                        "ON Flags\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", 240, 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "INSERT OR REPLACE INTO Flags (packageName, version, flagType, partitionId, user, name, stringVal, committed) VALUES (\"com.google.android.gms.car\", (SELECT version FROM ApplicationStates WHERE packageName=\"com.google.android.gms.car\"), 0, 0, \"\", \"app_white_list\", \"" + whiteListStringFinal + "\",1);\n" +
                                        "END;'"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method #4  --");

                        textView.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                        textView.setColorFilter(Color.argb(255,255,255,0));

                    } else {
                        suitableMethodFound = false;
                        appendText(logs, "\n\n--  Suitable method NOT found!  --");
                    }

                    // Check Start
                    if (suitableMethodFound) {
                        StreamLogs checkStep1 = runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'SELECT * FROM Flags WHERE name=\"app_white_list\";'"
                        );
                        String[] checkStep1Sorted = checkStep1.getInputStreamLog().split("\n");
                        Arrays.sort(checkStep1Sorted);

                        String checkStep1SortedToString = "";
                        for (String s : checkStep1Sorted) {
                            checkStep1SortedToString += "\n" + s;
                        }
                        checkStep1SortedToString.replaceFirst("\n", "");
                        checkStep1.setInputStreamLog(checkStep1SortedToString);

                        appendText(logs, "\n\n--  Check (1/3)  --" + checkStep1.getStreamLogsWithLabels());

                        appendText(logs, "\n--  Check (2/3)  --" + runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'DELETE FROM Flags WHERE name=\"app_white_list\";'"
                        ).getStreamLogsWithLabels());

                        StreamLogs checkStep3 = runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'SELECT * FROM Flags WHERE name=\"app_white_list\";'"
                        );
                        String[] checkStep3Sorted = checkStep3.getInputStreamLog().split("\n");
                        Arrays.sort(checkStep3Sorted);

                        String checkStep3SortedToString = "";
                        for (String s : checkStep3Sorted) {
                            checkStep3SortedToString += "\n" + s;
                        }
                        checkStep3SortedToString.replaceFirst("\n", "");
                        checkStep3.setInputStreamLog(checkStep3SortedToString);

                        appendText(logs, "\n--  Check (3/3)  --" + checkStep3.getStreamLogsWithLabels());

                        if (checkStep1.getInputStreamLog().length() == checkStep3.getInputStreamLog().length()) {
                            appendText(logs, "\n\n--  Check seems OK :)  --");

                        } else {
                            appendText(logs, "\n\n--  Check NOT OK.  --");
                            appendText(logs, "\n     Length before delete and after was not equal.");
                            appendText(logs, "\n        Before: " + checkStep1.getInputStreamLog().length());
                            appendText(logs, "\n        After:  " + checkStep3.getInputStreamLog().length());
                        }

                        save(true, "aa_patched_apps");
                    }
                    // Check End
                }
            }.start();
        }
    }

    public void patchforassistshort(final View view, final Button textView) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

            new Thread() {
                @Override
                public void run() {
                    String path = getApplicationInfo().dataDir;
                    boolean suitableMethodFound = true;
                    copyAssets();

                    appendText(logs, "\n\n-- Drop Triggers  --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS ASSIST_SHORT;'"
                    ).getStreamLogsWithLabels());

                    if (runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'\n").getInputStreamLog().equals("1")) {

                        appendText(logs, "\n\n--  run SQL method   --");
                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\",\"\",1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",\"\",1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);'" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);\n'"
                        ).getStreamLogsWithLabels());

                        appendText(logs, runSuWithCmd(
                                path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                        "'CREATE TRIGGER assist_short AFTER DELETE\n" +
                                        "ON FlagOverrides\n" +
                                        "BEGIN\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\",\"\",1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",\"\",1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);\n" +
                                        "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"LauncherShortcuts__assistant_shortcut_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);\n" +
                                "END;'\n"
                        ).getStreamLogsWithLabels());
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "assist_short");
                        textView.setText("Enable " + getText(R.string.enable_assistant_shortcuts));

                    } else {
                        suitableMethodFound = false;
                        appendText(logs, "\n\n--  Suitable method NOT found!  --");
                    }


                    
                }
            }.start();
        }

    public void patchrailassistant(final View view, final Button textView) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_assistant_rail;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);'\n"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_assistant_rail AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1) ,1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1) ,1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1) ,1,1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method   --");
                    textView.setText("Disable " + getText(R.string.enable_assistant_animation_in_navbar));

                    save(true, "aa_assistant_rail");

                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }



            }
        }.start();
    }

    public void patchforspeed(final View view, final Button textView) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_speed_hack;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'\n").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",\"\",99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",\"\",0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);'\n"

                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_speed_hack AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",\"\",99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",\"\",0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__enable\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__flake_filter_delay_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),99999999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ParkingStateSmoothing__telemetry_enabled_without_smoothing\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +

                                    "END;'"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    textView.setText("Disable " + getText(R.string.unlimited_scrolling_when_driving));
                    save(true, "aa_speed_hack");
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }



            }
        }.start();
    }

    public void patchfortouchlimit(final View view, final Button textView) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_six_tap;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\", \"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",\"\",0,1);\n" +


                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);'\n"

                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_six_tap AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",\"\",999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",\"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",\"\",0,1);\n" +


                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +

                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),999,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MesquiteFull__enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                            "END;'"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_six_tap");
                    textView.setText("Disable " + getText(R.string.disable_speed_limitations));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();
    }

    public void navpatch(View view, final Button textView) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_startup_policy;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM FLAGS WHERE name=\"SystemUi__startup_app_policy\";"+
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", \"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                                    path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_startup_policy AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",\"\",1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", \"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__start_in_launcher_if_no_user_selected_nav_app\",(SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),1,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUI__startup_app_policy\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_startup_policy");
                    textView.setText("Disable " + getText(R.string.navigation_at_start));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();


    }

    public void battOutline(View view, final Button textView) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        logs.setText(null);

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                boolean suitableMethodFound = true;
                copyAssets();

                appendText(logs, "\n\n-- Drop Triggers  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_battery_outline;'"
                ).getStreamLogsWithLabels());

                if (runSuWithCmd(
                        path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT 1 FROM ApplicationStates WHERE packageName=\"com.google.android.projection.gearhead\"'").getInputStreamLog().equals("1")) {

                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DELETE FROM Flags WHERE name=\"BatterySaver__icon_outline_enabled\";"+
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", \"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_battery_outline AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", \"\",0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 0,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 1,1),0,1);\n" +
                                    "INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM Flags WHERE packageName=\"com.google.android.projection.gearhead\" AND user LIKE \"%@%\" LIMIT 2,1),0,1);\n" +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    appendText(logs, "\n--  end SQL method  --");
                    save(true, "aa_battery_outline");
                    textView.setText("Disable " + getText(R.string.battery_outline_string));
                } else {
                    suitableMethodFound = false;
                    appendText(logs, "\n\n--  Suitable method NOT found!  --");
                }

            }
        }.start();


    }


    public static StreamLogs runSuWithCmd(String cmd) {
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        InputStream errorStream = null;

        StreamLogs streamLogs = new StreamLogs();
        streamLogs.setOutputStreamLog(cmd);

        try{
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            inputStream = su.getInputStream();
            errorStream = su.getErrorStream();
            outputStream.writeBytes(cmd + "\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            streamLogs.setInputStreamLog(readFully(inputStream));
            streamLogs.setErrorStreamLog(readFully(errorStream));
        } catch (IOException e){
            e.printStackTrace();
        }

        return streamLogs;
    }



    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }

    private void copyAssets() {
        String path = getApplicationInfo().dataDir;
        final TextView logs = findViewById(R.id.logs);
        File file = new File(path, "sqlite3");
        if (!file.exists()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logs.setText("\n--  Copy sqlite3 to data directory  --");
                }
            });
            InputStream in;
            OutputStream out;
            try {
                in = this.getResources().openRawResource(R.raw.sqlite3);

                String outDir = getApplicationInfo().dataDir;

                File outFile = new File(outDir, "sqlite3");

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch(IOException e) {
                Log.e("eselter", "Failed to copy asset file: sqlite3", e);
            }
        }
        appendText(logs, "\n--  chmod 775 sqlite3  --");
        appendText(logs, runSuWithCmd("chmod 775 " + path + "/sqlite3").getStreamLogsWithLabels());
        appendText(logs, "\n--  end chmod 775 sqlite3  --");
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    private void appendText(final TextView textView, final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(s);
            }
        });
    }





}