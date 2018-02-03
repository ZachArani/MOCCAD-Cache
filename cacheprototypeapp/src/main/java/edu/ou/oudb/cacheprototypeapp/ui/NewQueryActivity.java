package edu.ou.oudb.cacheprototypeapp.ui;

import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import edu.ou.oudb.cacheprototypeapp.R;
import edu.ou.oudb.cacheprototypeapp.provider.ProcessedQueryDbHelper;
import edu.ou.oudb.cacheprototypelibrary.metadata.Metadata;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.InvalidPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.TrivialPredicateException;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Predicate;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.PredicateFactory;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;

/*Not used*/
public class NewQueryActivity extends Activity implements OnClickListener {


    private static int Id = 1;

    private Button mRelationChoiceButton = null;
    private Button mCancelButton = null;
    private Button mLaunchButton = null;
    private LinearLayout mNewPredicateViewList = null;
    private ImageButton mNewPredicateButton = null;
    private String mSelectedRelation = null;
    private Set<String> mRelations = null;
    private List<String> mAttributes = null;
    private ArrayAdapter<String> mRelationAdapter = null;
    private ArrayAdapter<String> mAttributeAdapter = null;
    private ProcessedQueryDbHelper mDBHelper = null;

    private SparseArray<PredicateHolder> mViewIdPredicateHolderMap = new SparseArray<PredicateHolder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_query);

        mDBHelper = new ProcessedQueryDbHelper(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        getRelationChoiceButton().setOnClickListener(this);

        getNewPredicateButton().setOnClickListener(this);

        getNewPredicateButton().setEnabled(false);

        getCancelButton().setOnClickListener(this);

        getLaunchButton().setOnClickListener(this);

        getLaunchButton().setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRelations = Metadata.getInstance().getRelationNames();

        mRelationAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, mRelations.toArray(new String[mRelations.size()]));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_query, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startButton:
                launchQuery();
                break;
            case R.id.cancelButton:
                cancelQueryLaunch();
                break;
            case R.id.relation_choice_button:
                chooseRelation();
                break;
            case R.id.addButton:
                addPredicate();
                break;
            case R.id.deletePredicateButton:
                deletePredicate(v);
                break;
            case R.id.att_left_choice:
            case R.id.att_right_choice:
                chooseAttribute(v);
                break;
            case R.id.operator:
                chooseOperator(v);
                break;
        }
    }

    private void deletePredicate(View v) {

        LinearLayout viewToRemove = (LinearLayout) v.getParent();

        mViewIdPredicateHolderMap.delete(viewToRemove.getId());

        getNewPredicateViewList().removeView(viewToRemove);
    }

    private void addPredicate() {

        final String[] predicatesTypes = getResources().getStringArray(R.array.predicate_types);

        AlertDialog.Builder builder = new AlertDialog.Builder(NewQueryActivity.this);

        builder.setTitle(getString(R.string.choose_predicate));

        builder.setItems(R.array.predicate_types,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        String predicateType = predicatesTypes[position];
                        View predicate = null;

                        if (predicateType.equals(getString(R.string.XopCPredicate))) {
                            predicate = createXopCPredicateView();
                        } else //if(predicateType.equals(getString(R.string.XopYPredicate)))
                        {
                            predicate = createXopYPredicateView();
                        }

                        predicate.setId(Id++);

                        mViewIdPredicateHolderMap.append(predicate.getId(), new PredicateHolder());

                        getNewPredicateViewList().addView(predicate);

                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void chooseRelation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewQueryActivity.this);

        builder.setTitle(getString(R.string.choose_relation));

        builder.setAdapter(mRelationAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        String selectedRelation = mRelationAdapter.getItem(position);
                        if (selectedRelation != mSelectedRelation) {
                            mSelectedRelation = selectedRelation;

                            mAttributes = Metadata.getInstance().getRelationMetadata(mSelectedRelation).getAttributeNames();

                            mAttributeAdapter = new ArrayAdapter<String>(
                                    NewQueryActivity.this,
                                    android.R.layout.select_dialog_item,
                                    mAttributes.toArray(new String[mAttributes.size()]));

                            getRelationChoiceButton().setText(mSelectedRelation);
                            //remove all predicates
                            if (getNewPredicateViewList().getChildCount() > 0) {
                                getNewPredicateViewList().removeAllViews();
                            }

                            mViewIdPredicateHolderMap.clear();

                            getNewPredicateButton().setEnabled(true);
                            getLaunchButton().setEnabled(true);

                        }
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void chooseAttribute(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewQueryActivity.this);

        builder.setTitle(getString(R.string.choose_attribute));

        builder.setAdapter(mAttributeAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        LinearLayout predicateView = (LinearLayout) v.getParent();

                        String selectedAttribute = mAttributeAdapter.getItem(position);

                        String currentAttribute;

                        switch (v.getId()) {
                            case R.id.att_left_choice:
                                currentAttribute = mViewIdPredicateHolderMap.get(predicateView.getId()).operandLeft;
                                if (selectedAttribute != currentAttribute) {
                                    mViewIdPredicateHolderMap.get(predicateView.getId()).operandLeft = selectedAttribute;
                                    ((Button) v).setText(selectedAttribute);
                                }
                                break;
                            case R.id.att_right_choice:
                                currentAttribute = mViewIdPredicateHolderMap.get(predicateView.getId()).operandRight;
                                if (selectedAttribute != currentAttribute) {
                                    mViewIdPredicateHolderMap.get(predicateView.getId()).operandRight = selectedAttribute;
                                    ((Button) v).setText(selectedAttribute);
                                }

                                break;
                        }


                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void chooseOperator(final View v) {
        final String[] operators = getResources().getStringArray(R.array.operators);

        AlertDialog.Builder builder = new AlertDialog.Builder(NewQueryActivity.this);

        builder.setTitle(getString(R.string.choose_attribute));

        builder.setItems(R.array.operators,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        LinearLayout predicateView = (LinearLayout) v.getParent();

                        String selectedOperator = operators[position];

                        String currentOperator = mViewIdPredicateHolderMap.get(predicateView.getId()).operator;

                        if (selectedOperator != currentOperator) {
                            mViewIdPredicateHolderMap.get(predicateView.getId()).operator = selectedOperator;
                            ((Button) v).setText(selectedOperator);
                        }
                        dialog.dismiss();
                    }
                });

        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void cancelQueryLaunch() {
        NavUtils.navigateUpFromSameTask(NewQueryActivity.this);
    }

    private void launchQuery() {

        int predicateArraySize = mViewIdPredicateHolderMap.size();
        boolean areValidPredicates = true;
        Query query = new Query(mSelectedRelation);

        PredicateHolder curPredicateHolder;

        for (int i = 0; i < predicateArraySize && areValidPredicates; ++i) {
            curPredicateHolder = mViewIdPredicateHolderMap.valueAt(i);
            try {
                Predicate predicate = PredicateFactory.createPredicate(
                        curPredicateHolder.operandLeft,
                        curPredicateHolder.operator,
                        curPredicateHolder.operandRight
                );
                query.addPredicate(predicate);

            } catch (TrivialPredicateException e) {
            } catch (InvalidPredicateException e) {
                launchErrorDialog(getString(R.string.invalid_predicate), getString(R.string.invalid_predicate_message) + curPredicateHolder);
                areValidPredicates = false;
            }
        }
        //if no errors
        if (areValidPredicates) {
            // add query to local database of previously ran queries
            mDBHelper.addQuery(query);

            //process query
            (new QueryProcessTask(this)).execute(query);

        }


    }

    private void launchErrorDialog(String title, String message) {
        ErrorDialog errorDialog = ErrorDialog.newInstance(title, message);
        errorDialog.show(getFragmentManager(), "error_dialog");
    }

    private View createXopCPredicateView() {
        View predicateView = getLayoutInflater().inflate(R.layout.new_xopcpredicate_view, getNewPredicateViewList(), false);

        ImageButton deletePredicateButton = (ImageButton) predicateView.findViewById(R.id.deletePredicateButton);
        deletePredicateButton.setOnClickListener(this);

        Button chooseAttributeLeftButton = (Button) predicateView.findViewById(R.id.att_left_choice);
        chooseAttributeLeftButton.setOnClickListener(this);

        Button chooseOperatorLeftButton = (Button) predicateView.findViewById(R.id.operator);
        chooseOperatorLeftButton.setOnClickListener(this);

        EditText chooseValueRightEditText = (EditText) predicateView.findViewById(R.id.att_right_value);
        chooseValueRightEditText.addTextChangedListener(new OnXopCPredicateValueTextWatcher(chooseValueRightEditText));


        return predicateView;
    }

    private View createXopYPredicateView() {
        View predicateView = getLayoutInflater().inflate(R.layout.new_xopypredicate_view, getNewPredicateViewList(), false);

        ImageButton deletePredicateButton = (ImageButton) predicateView.findViewById(R.id.deletePredicateButton);
        deletePredicateButton.setOnClickListener(this);

        Button chooseAttributeLeftButton = (Button) predicateView.findViewById(R.id.att_left_choice);
        chooseAttributeLeftButton.setOnClickListener(this);

        Button chooseOperatorLeftButton = (Button) predicateView.findViewById(R.id.operator);
        chooseOperatorLeftButton.setOnClickListener(this);

        Button chooseAttributeRightButton = (Button) predicateView.findViewById(R.id.att_right_choice);
        chooseAttributeRightButton.setOnClickListener(this);

        return predicateView;
    }

    public Button getCancelButton() {
        if (mCancelButton == null) {
            mCancelButton = (Button) findViewById(R.id.cancelButton);
        }

        return mCancelButton;
    }

    public Button getLaunchButton() {
        if (mLaunchButton == null) {
            mLaunchButton = (Button) findViewById(R.id.startButton);
        }

        return mLaunchButton;
    }

    public Button getRelationChoiceButton() {
        if (mRelationChoiceButton == null) {
            mRelationChoiceButton = (Button) findViewById(R.id.relation_choice_button);
        }

        return mRelationChoiceButton;
    }

    public LinearLayout getNewPredicateViewList() {
        if (mNewPredicateViewList == null) {
            mNewPredicateViewList = (LinearLayout) findViewById(R.id.new_predicate_view_list);
        }

        return mNewPredicateViewList;
    }

    public String getSelectedRelation() {
        return mSelectedRelation;
    }

    /**
     * @return the newPredicateButton
     */
    public ImageButton getNewPredicateButton() {

        if (mNewPredicateButton == null) {
            mNewPredicateButton = (ImageButton) findViewById(R.id.addButton);
        }

        return mNewPredicateButton;
    }

    public class OnXopCPredicateValueTextWatcher implements TextWatcher {
        EditText mEditTextView;

        public OnXopCPredicateValueTextWatcher(View v) {
            mEditTextView = (EditText) v;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            LinearLayout predicateView = (LinearLayout) mEditTextView.getParent();

            String selectedValue = s.toString();

            String currentValue = mViewIdPredicateHolderMap.get(predicateView.getId()).operandRight;

            if (selectedValue != currentValue) {
                mViewIdPredicateHolderMap.get(predicateView.getId()).operandRight = selectedValue;
            }
        }
    }


    private class PredicateHolder {
        public String operandLeft;
        public String operator;
        public String operandRight;

        @Override
        public String toString() {
            return (new StringBuilder())
                    .append(operandLeft)
                    .append(' ')
                    .append(operator)
                    .append(' ')
                    .append(operandRight)
                    .toString();
        }
    }

}
