package holmes.gradebook;

import android.app.Application;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import dagger.Module;
import dagger.Provides;

@Module(injects = GradebookActivity.class)
public class GradebookModule {
  private final Application application;

  public GradebookModule(Application application) {
    this.application = application;
  }

  @Provides Application getApplication() {
    return application;
  }

  @Provides GoogleApiClient provideGoogleApiClient(Application application) {
    return new GoogleApiClient.Builder(application)
        .addApi(Drive.API)
        .addScope(Drive.SCOPE_FILE)
        .build();
  }
}
