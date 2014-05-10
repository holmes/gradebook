package holmes.gradebook;

import android.app.Application;
import android.content.Context;
import dagger.ObjectGraph;

public class GradebookApplication extends Application {
  private ObjectGraph objectGraph;

  @Override public void onCreate() {
    super.onCreate();
    objectGraph = ObjectGraph.create(new GradebookModule(this));
  }

  public static ObjectGraph getObjectGraph(Context context) {
    return ((GradebookApplication) context.getApplicationContext()).objectGraph;
  }
}
