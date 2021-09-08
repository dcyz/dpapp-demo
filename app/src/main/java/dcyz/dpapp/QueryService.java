package dcyz.dpapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class QueryService extends Service {
    public QueryService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}