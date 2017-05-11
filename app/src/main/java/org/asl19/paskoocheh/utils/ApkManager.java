package org.asl19.paskoocheh.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.asl19.paskoocheh.BuildConfig;
import org.asl19.paskoocheh.Constants;
import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.toollist.ToolListFragment;

import java.io.File;

/**
 * This class handles the installation and uninstallation
 * of applications.
 */
public class ApkManager {

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    /**
     * Create an ApkManager
     **/
    public ApkManager() {
    }

    /**
     * Install the given application file iff the given checksum matches or is empty.
     *
     * @param checksum A checksum for the given application.
     * @param file A apk for which to install.
     * @return true iff the given apk was installed.
     */
    public static boolean installPackage(Context context, String checksum, File file) {
        if (checksum.isEmpty() || Checksum.checkChecksum(checksum, file)) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
            bundle.putString(Constants.TOOL_ID, file.getName());
            FirebaseAnalytics.getInstance(context).logEvent(Constants.INSTALL, bundle);

            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri internalUri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= 24) {
                internalUri = FileProvider.getUriForFile(context, AUTHORITY, file);
            }

            intent.setDataAndType(internalUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
            return true;
        } else {
            Toast.makeText(context, R.string.checksum_invalid, Toast.LENGTH_LONG).show();
            file.delete();

            return false;
        }

    }

    /**
     * Uninstall the application with the given package name.
     *
     * @param packageName Package name of application to be uninstalled.
     */
    public static void uninstallPackage(Context context, String packageName) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SCREEN, ToolListFragment.TAG);
        bundle.putString(Constants.TOOL_ID, packageName);
        FirebaseAnalytics.getInstance(context).logEvent(Constants.UNINSTALL, bundle);

        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
