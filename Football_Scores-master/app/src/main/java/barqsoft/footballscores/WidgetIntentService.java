package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WidgetIntentService extends IntentService {
    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL
    };
    private static final int INDEX_HOME= 0;
    private static final int INDEX_AWAY = 1;
    private static final int INDEX_HOME_GOALS = 2;
    private static final int INDEX_AWAY_GOALS = 3;

    private String[] fragmentdate = new String[1];



    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                FootballAppWidget.class));

        // Get today's data from the ContentProvider
        Uri scoresForDateUri = DatabaseContract.scores_table.buildScoreWithDate();
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Date fragmentdate = new Date(System.currentTimeMillis());
        Date fragmentdateYesterday = new Date(System.currentTimeMillis()-24*60*60*1000);
        SimpleDateFormat mformat = new SimpleDateFormat(getString(R.string.simple_date_format));
        String date = mformat.format(fragmentdate);
        String dateYesterday = mformat.format(fragmentdateYesterday);  //get recent matches today and yesterday
        Cursor data = getApplicationContext().getContentResolver().query(scoresForDateUri, SCORES_COLUMNS, null,
                new String[]{ dateYesterday}, DatabaseContract.scores_table.DATE_COL + " ASC");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract data from the Cursor
        String home = data.getString(INDEX_HOME);
        String away = data.getString(INDEX_AWAY);
        String home_score = data.getString(INDEX_HOME_GOALS);
        String away_score = data.getString(INDEX_AWAY_GOALS);
        int homeArtResourceId = Utilies.getTeamCrestByTeamName(home);
        int awayArtResourceId = Utilies.getTeamCrestByTeamName(away);
        String formattedScore = Utilies.getScores(Integer.parseInt(home_score),Integer.parseInt(away_score), getApplicationContext());
        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.football_app_widget;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            views.setImageViewResource(R.id.widget_icon, homeArtResourceId);
//            // Content Descriptions for RemoteViews were only added in ICS MR1
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                setRemoteContentDescription(views,"FootballScore:" + home + formattedScore + away );
//            }
            views.setTextViewText(R.id.appwidget_text1, home);
            views.setTextViewText(R.id.appwidget_text2, away);
            views.setTextViewText(R.id.scoreTextView, formattedScore);
            views.setImageViewResource(R.id.widget_icon, homeArtResourceId);
            views.setImageViewResource(R.id.widget_icon2, awayArtResourceId);


            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

}
