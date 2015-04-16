/*
 * @(#)EmpruntActivity.java
 *
 * Copyright (c) 2011-2012 Erik C. Thauvin (http://erik.thauvin.net/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the authors nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $Id$
 *
 */
package net.thauvin.erik.android.emprunt;

import java.text.NumberFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The <code>EmpruntActivity</code> class implements a simple loan payment calculator.
 * 
 * @author <a href="mailto:erik@thauvin.net">Erik C. Thauvin</a>
 * @version $Revision$
 * @created October 14, 2011
 * @since 1.0
 */
public class EmpruntActivity extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		final TextView titleBar = (TextView) findViewById(R.id.titleBar);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			titleBar.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 0));
			titleBar.setVisibility(View.INVISIBLE);
		}

		final TextView paymentFld = (TextView) findViewById(R.id.payment);
		final TextView costFld = (TextView) findViewById(R.id.cost);

		final EditText amountFld = ((EditText) findViewById(R.id.amount));
		final EditText interestFld = ((EditText) findViewById(R.id.interest));
		final EditText monthsFld = ((EditText) findViewById(R.id.months));

		final Drawable imgX = getResources().getDrawable(android.R.drawable.presence_offline);

		final TextWatcher tw = new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				amountFld.setTextColor(Color.BLACK);
				interestFld.setTextColor(Color.BLACK);
				monthsFld.setTextColor(Color.BLACK);

				paymentFld.setText(R.string.hint_calculate);
				paymentFld.setTextColor(Color.LTGRAY);

				costFld.setText(R.string.hint_calculate);
				costFld.setTextColor(Color.LTGRAY);

				manageClearButton(amountFld, imgX);
				manageClearButton(interestFld, imgX);
				manageClearButton(monthsFld, imgX);
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub
			}
		};

		amountFld.addTextChangedListener(tw);
		interestFld.addTextChangedListener(tw);
		monthsFld.addTextChangedListener(tw);

		amountFld.setOnTouchListener(buildOnTouchListener(amountFld, imgX));
		interestFld.setOnTouchListener(buildOnTouchListener(interestFld, imgX));
		monthsFld.setOnTouchListener(buildOnTouchListener(monthsFld, imgX));

		final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		final Button button = (Button) findViewById(R.id.calculate);
		button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Hide the soft keyboard
				imm.hideSoftInputFromWindow(button.getApplicationWindowToken(), 0);

				final String amount = amountFld.getText().toString();
				final String interest = interestFld.getText().toString();
				final String months = monthsFld.getText().toString();

				if (amount.isEmpty())
				{
					amountFld.requestFocus();
				}
				else if (interest.isEmpty())
				{
					interestFld.requestFocus();
				}
				else if (months.isEmpty())
				{
					monthsFld.requestFocus();
				}
				else
				{
					amountFld.requestFocus();

					final int capital = Integer.valueOf(amount);

					if (capital > 0)
					{
						final double rate = Double.valueOf(interest);

						if (rate > 0)
						{
							final int term = Integer.valueOf(months);

							if (term > 0)
							{
								final double mrate = (rate / 100) / 12;
								final double payment = (capital * mrate) / (1 - Math.pow((1 + mrate), -term));
								final double cost = (payment * term) - capital;

								final Editable s = Editable.Factory.getInstance().newEditable(
										NumberFormat.getCurrencyInstance().format(payment));

								s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
								paymentFld.setTextColor(Color.BLACK);
								paymentFld.setText(s);

								costFld.setTextColor(Color.BLACK);
								costFld.setText(NumberFormat.getCurrencyInstance().format(cost));
							}
							else
							{
								monthsFld.setTextColor(Color.RED);
								monthsFld.requestFocus();
							}
						}
						else
						{
							interestFld.setTextColor(Color.RED);
							interestFld.requestFocus();
						}
					}
					else
					{
						amountFld.setTextColor(Color.RED);
						amountFld.requestFocus();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0, 0, 0, R.string.menu_about).setIcon(android.R.drawable.ic_menu_info_details);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == 0)
		{
			AlertDialog builder;
			try
			{
				builder = AboutDialogBuilder.create(this);
				builder.show();
			}
			catch (NameNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * Manages the clear button.
	 * 
	 * @param view The text view.
	 * @param img The image.
	 */
	private void manageClearButton(TextView view, Drawable img)
	{
		if (view.getText().toString().equals(""))
		{
			view.setCompoundDrawablesWithIntrinsicBounds(view.getCompoundDrawables()[0], view.getCompoundDrawables()[1], null,
					view.getCompoundDrawables()[3]);
		}
		else
		{
			view.setCompoundDrawablesWithIntrinsicBounds(view.getCompoundDrawables()[0], view.getCompoundDrawables()[1], img,
					view.getCompoundDrawables()[3]);
		}
	}

	/**
	 * Builds OnTouchListener for clear buttons.
	 * 
	 * @param field The EditText field.
	 * @return A new OnTouchListener.
	 */
	private OnTouchListener buildOnTouchListener(final EditText field, final Drawable img)
	{
		return new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				// Is there an X showing?
				if (field.getCompoundDrawables()[2] == null)
				{
					return false;
				}

				// Only do this for up touches
				if (event.getAction() != MotionEvent.ACTION_UP)
				{
					return false;
				}

				// Is touch one of our clear buttons?
				if (event.getX() > field.getWidth() - field.getPaddingRight() - img.getIntrinsicWidth())
				{
					field.requestFocusFromTouch();
					field.setText("");
				}

				return false;
			}
		};
	}
}
