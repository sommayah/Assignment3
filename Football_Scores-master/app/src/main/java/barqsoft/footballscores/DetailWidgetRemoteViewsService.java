package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] SCORES_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_ID
    };
    private static final int INDEX_HOME= 0;
    private static final int INDEX_AWAY = 1;
    private static final int INDEX_HOME_GOALS = 2;
    private static final int INDEX_AWAY_GOALS = 3;
    private static final int INDEX_MATCH_ID = 4;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Uri footballScoreUri = DatabaseContract.scores_table.buildScoreWithDate();
                Date fragmentdate = new Date(System.currentTimeMillis());
                Date fragmentdateYesterday = new Date(System.currentTimeMillis()-24*60*60*1000);
                SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                String date = mformat.format(fragmentdate);
                String yesterdayDate = mformat.format(fragmentdateYesterday);
               // String dateYesterday = mformat.format(fragmentdateYesterday);  //get recent matches today and yesterday
                data = getContentResolver().query(footballScoreUri,
                        SCORES_COLUMNS,
                        null,
                        new String[]{date},
                        DatabaseContract.scores_table.DATE_COL+ " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                String home = data.getString(INDEX_HOME);
                String away = data.getString(INDEX_AWAY);
                String home_score = data.getString(INDEX_HOME_GOALS);
                String away_score = data.getString(INDEX_AWAY_GOALS);
                int homeArtResourceId = Utilies.getTeamCrestByTeamName(home);
                int awayArtResourceId = Utilies.getTeamCrestByTeamName(away);
                String formattedScore = Utilies.getScores(Integer.parseInt(home_score),Integer.parseInt(away_score), getApplicationContext());
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//                    setRemoteContentDescription(views, home + formattedScore + away);
//                }
                views.setTextViewText(R.id.appwidget_text1, home);
                views.setTextViewText(R.id.appwidget_text2, away);
                views.setTextViewText(R.id.scoreTextView, formattedScore);
                views.setImageViewResource(R.id.widget_icon, homeArtResourceId);
                views.setImageViewResource(R.id.widget_icon2, awayArtResourceId);

                final Intent fillInIntent = new Intent();

                Uri footballUri = DatabaseContract.scores_table.buildScoreWithDate();
                fillInIntent.setData(footballUri);
                Intent launchIntent = new Intent(getApplicationContext(),MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.widget, pendingIntent);
                views.setOnClickFillInIntent(R.id.widget_list_item, launchIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_icon, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}