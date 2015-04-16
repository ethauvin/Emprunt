/*
 * @(#)AboutDialogBuilder.java
 *
 * $Id$
 *
 */
package net.thauvin.erik.android.emprunt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * The <code>AboutDialogBuilder</code> class implements a simple "About" dialog.
 * 
 * @author <a href="http://itkrauts.com/archives/26-Creating-a-simple-About-Dialog-in-Android-1.6.html">Sebastian Bauer</a>
 * @author <a href="mailto:erik@thauvin.net">Erik C. Thauvin</a>
 * @version $Revision$
 * @created October 14, 2011
 * @since 1.0
 */
public class AboutDialogBuilder
{
	public static AlertDialog create(Context context) throws NameNotFoundException
	{
		// Try to load the a package matching the name of our own package
		final PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		final String versionInfo = pInfo.versionName;

		final String aboutTitle = String.format("%s%s", context.getString(R.string.about_title), context.getString(R.string.app_name));
		final String versionString = String.format("%s %s", context.getString(R.string.label_version), versionInfo);

		// Set up the TextView
		final TextView message = new TextView(context);
		// message.setTextSize(18);
		message.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		message.setBackgroundColor(Color.WHITE);

		// We'll use a spannablestring to be able to make links clickable
		final SpannableString s;
		if (System.getProperty("os.name").equalsIgnoreCase("qnx"))
		{
			s = new SpannableString(context.getString(R.string.about_copyright_blackberry));
		}
		else
		{
			s = new SpannableString(context.getString(R.string.about_copyright));
		}

		// Get screen metrics
		final DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

		// Set some padding
		final int pad = (int) ((10 / metrics.density) + 0.5f);
		message.setPadding(pad, pad, pad, pad * 2);

		// Set up the final string
		message.setText(versionString + "\n\n" + s);

		// Now linkify the text
		Linkify.addLinks(message, Linkify.ALL);

		return new AlertDialog.Builder(context).setTitle(aboutTitle).setCancelable(true).setIcon(R.drawable.icon)
				.setPositiveButton(context.getString(android.R.string.ok), null).setView(message).create();
	}

}