package holmes.gradebook;

import android.app.Activity;
import android.os.Bundle;
import javax.inject.Inject;

public class GradebookActivity extends Activity {

  @Inject ApiConnector apiConnector;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GradebookApplication.getObjectGraph(this).inject(this);
    setContentView(R.layout.activity_gradebook);
  }

  @Override protected void onStart() {
    super.onStart();
    apiConnector.connect(this);
  }

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onStop() {
    super.onStop();
    apiConnector.disconnect();
  }
}
