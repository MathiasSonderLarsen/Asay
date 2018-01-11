package net.initiativet.a62415_team8;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
       implements ProposalListFragment.OnProposalListInteractionListener, DetailFragment.OnDetailFragmentInteractionListener, View.OnClickListener {

    ProposalDAO propDAO = new ProposalDAO();
    List<ProposalDTO> propList = new ArrayList<ProposalDTO>();
    ArrayList<String> titleList = new ArrayList<String>();
    TextView titleTV;
    ImageButton backButton;
    Boolean backButtonActivated;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleTV = (TextView) findViewById(R.id.actionbar_title);
        titleTV.setText("Udvalgte forslag");

        backButton = (ImageButton) findViewById(R.id.actionbar_button_image);
        backButton.setBackgroundResource(R.drawable.map);
        backButton.setOnClickListener(this);
        backButtonActivated = false;

        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    propList = propDAO.fetchProposal("http://oda.ft.dk/api/Sag?&$filter=typeid eq 3 and periodeid eq 146");
                }catch (Exception e) {
                    System.out.println(e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                for(int i = 0; i < propList.size(); i++) {
                    titleList.add(propList.get(i).getNummer() + " : " + propList.get(i).getTitelKort());
                }

                if(savedInstanceState == null){
                    ProposalListFragment lfl = new ProposalListFragment();

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("titleList",titleList);
                    lfl.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragmentContainer, lfl)
                            .addToBackStack(null)
                            .commit();
                }

            }
        }.execute();


    }

    @Override
    public void onProposalListInteraction(int position) {

        titleTV.setText(propList.get(position).getNummer());

        backButton.setBackgroundResource(R.drawable.arrow_left);
        backButtonActivated = true;
        DetailFragment df = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", propList.get(position).getTitel());
        bundle.putString("titleKort", propList.get(position).getTitelKort());
        bundle.putString("nummer", propList.get(position).getNummer());
        bundle.putString("resume", propList.get(position).getResume());
        bundle.putString("nummerPrefix", propList.get(position).getNummerPrefix());
        bundle.putString("nummerPostfix", propList.get(position).getNummerpostfix());
        bundle.putString("nummernumerisk", propList.get(position).getNummernumerisk());

        df.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, df)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDetailFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        if(view == backButton){
            if(backButtonActivated == true){
                getSupportFragmentManager().popBackStack();
                backButtonActivated = false;
                backButton.setBackgroundResource(R.drawable.map);
                titleTV.setText("Udvalgte forslag");
            }
        }
    }
}
