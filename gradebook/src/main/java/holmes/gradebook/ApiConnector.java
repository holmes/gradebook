package holmes.gradebook;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.android.gms.common.api.GoogleApiClient.Builder;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

@Singleton
public class ApiConnector {
  // Request code to use when launching the resolution activity
  private static final int REQUEST_RESOLVE_ERROR = 1001;
  // Unique tag for the error dialog fragment
  private static final String DIALOG_ERROR = "dialog_error";

  private final ApiListener apiListener;
  private Activity activity;
  private GoogleApiClient apiClient;
  private boolean resolvingError;

  @Inject
  public ApiConnector() {
    this.apiListener = new ApiListener();
    this.resolvingError = false;
  }

  public void connect(Activity activity) {
    this.activity = activity;

    if (apiClient == null) {
      this.apiClient = new Builder(activity).addApi(Drive.API)
          .addScope(Drive.SCOPE_FILE)
          .addConnectionCallbacks(apiListener)
          .addOnConnectionFailedListener(apiListener)
          .build();
    } else {
      this.apiClient.reconnect();
    }
  }

  public void disconnect() {
    if (this.apiClient != null) {
      this.apiClient.disconnect();
    }
  }

  private class ApiListener implements ConnectionCallbacks, OnConnectionFailedListener {
    @Override public void onConnected(Bundle bundle) {

    }

    @Override public void onConnectionSuspended(int i) {

    }

    @Override public void onConnectionFailed(ConnectionResult result) {
      if (resolvingError) {
        // Already attempting to resolve an error.
        return;
      } else if (result.hasResolution()) {
        try {
          resolvingError = true;
          result.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
        } catch (IntentSender.SendIntentException e) {
          // There was an error with the resolution intent. Try again.
          apiClient.connect();
        }
      } else {
        // Show dialog using GooglePlayServicesUtil.getErrorDialog()
        showErrorDialog(result.getErrorCode());
        resolvingError = true;
      }
    }
  }

  /* Creates a dialog for an error message */
  private void showErrorDialog(int errorCode) {
    // Create a fragment for the error dialog
    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
    // Pass the error that should be displayed
    Bundle args = new Bundle();
    args.putInt(DIALOG_ERROR, errorCode);
    dialogFragment.setArguments(args);
    dialogFragment.show(activity.getFragmentManager(), "errordialog");
  }

  /** Called from ErrorDialogFragment when the dialog is dismissed. */
  public void onErrorDialogDismissed() {
    resolvingError = false;
  }

  /** A fragment to display an error dialog */
  public static class ErrorDialogFragment extends DialogFragment {
    public ErrorDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Get the error code and retrieve the appropriate dialog
      int errorCode = this.getArguments().getInt(DIALOG_ERROR);
      return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(),
          REQUEST_RESOLVE_ERROR);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
      GradebookApplication.getObjectGraph(getActivity())
          .get(ApiConnector.class)
          .onErrorDialogDismissed();
    }
  }
}
