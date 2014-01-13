package com.eecs448.kuville;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @written Brent Mitchel
 * @tested Brent Mitchell
 * @debugged Brent Mitchell
 */
public class Upgrades {
	private Context c;
	private String[] buildingNames;

	public Upgrades(Context c) {
		this.c = c;
	}

	public void createUpgrades() {
		StoredUpgrades db = new StoredUpgrades(c);
		buildingNames = c.getResources().getStringArray(R.array.buildingname);
		db.open();
		for (int i = 0; i < buildingNames.length; i++)
			db.createEntry(buildingNames[i]);
		db.setUpgraded(buildingNames[0]);
		db.close();
	}

	public void displayUpgrades(LinearLayout layout) {
		StoredUpgrades db = new StoredUpgrades(c);
		buildingNames = c.getResources().getStringArray(R.array.buildingname);
		db.open();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < buildingNames.length; i++) {
			CheckedTextView tv = new CheckedTextView(c);
			tv.setLayoutParams(lp);

			String left = buildingNames[i];
			String right = 5 * i + " gold";

			// Sets name of upgrade and gold value at same starting points on
			// line
			SpannableString styledText = new SpannableString(left + "\n\n"
					+ right);
			styledText.setSpan(new AlignmentSpan.Standard(
					Alignment.ALIGN_OPPOSITE), left.length() + 2, left.length()
					+ 2 + right.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			tv.setText(styledText);
			tv.setTag(buildingNames[i]);

			// Checks to see if building has already been upgraded
			// Sets checkmark if it has not been upgraded
			if (db.isUpgraded(buildingNames[i]))
				tv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
			else
				tv.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);

			tv.setBackgroundResource(R.drawable.background);
			setListener(tv);

			layout.addView(tv);
		}

		db.close();
	}

	private void setListener(CheckedTextView tv) {
		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				CheckedTextView tv = (CheckedTextView) view;

				Context context = view.getContext();
				SharedPreferences sharedPreferences = context
						.getApplicationContext().getSharedPreferences(
								"com.eecs448.kuville", Context.MODE_PRIVATE);

				int gold = sharedPreferences.getInt("gold", 0);
				StoredUpgrades db = new StoredUpgrades(view.getContext());

				db.open();
				if (!db.isUpgraded(tv.getTag().toString())) {
					int subtractGold = 5 * (db.getRowId(tv.getTag().toString()) - 1);
					if (gold >= subtractGold) {
						View topView = (View) view.getParent().getParent()
								.getParent().getParent().getParent();
						TextView goldTextview = (TextView) topView
								.findViewById(R.id.gold);

						tv.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
						ViewGroup parent = (ViewGroup) tv.getParent();
						int index = parent.indexOfChild(tv);
						// Removes the upgrade without checkbox
						parent.removeView(tv);
						// Replaces with upgrade with checkbox marked
						parent.addView(tv, index);

						db.setUpgraded(tv.getTag().toString());
						// Decreases gold when user upgrades based
						// on the amount of the upgrade
						goldTextview.setText(Integer.toString(gold
								- subtractGold));

						SharedPreferences.Editor e = sharedPreferences.edit();
						e.putInt("gold", gold - subtractGold);
						e.commit();
					}

				}
				db.close();

			}
		});
	}
}
