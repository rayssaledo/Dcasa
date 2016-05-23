package projeto.emp.dcasa.views;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import projeto.emp.dcasa.R;
import projeto.emp.dcasa.models.PROFESSIONAL_TYPE;
import projeto.emp.dcasa.models.Professional;
import projeto.emp.dcasa.models.User;
import projeto.emp.dcasa.utils.MainMapFragment;

public class MapsActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private List<Professional> professionals;
    private List<Professional> professionalsSelected;
    List<PROFESSIONAL_TYPE> typesList;
    private ImageButton ib_electrician;
    private ImageButton ib_fitter;
    private ImageButton ib_plumber;
    private Boolean electrician_pressed;
    private Boolean plumber_pressed;
    private Boolean fitter_pressed;
    private Button btn_call;
    private TextView tv_profession;
    private TextView tv_name_professional;
    private TextView tv_cpf;
    private TextView tv_phone_number;
    private View infoWindow;

    private MainMapFragment mapFragment;
    private HashMap<Marker, Professional> professionalMarkerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = new MainMapFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.map, mapFragment);
        ft.commit();

        electrician_pressed = true;
        plumber_pressed = true;
        fitter_pressed = true;
        typesList = new ArrayList<PROFESSIONAL_TYPE>();
        typesList.add(PROFESSIONAL_TYPE.ELECTRICIAN);
        typesList.add(PROFESSIONAL_TYPE.FITTER);
        typesList.add(PROFESSIONAL_TYPE.PLUMBER);

        if  ( mGoogleApiClient ==  null )  {
            mGoogleApiClient =  new  GoogleApiClient. Builder ( this )
                    . addConnectionCallbacks ( this )
                    . addOnConnectionFailedListener ( this )
                    . addApi ( LocationServices. API )
                    . build ();
        }

        professionals = criaProfissionais();

        ib_electrician = (ImageButton) findViewById(R.id.ib_electrician);
        ib_electrician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (electrician_pressed) {
                    electrician_pressed = false;
                    ib_electrician.setImageResource(R.mipmap.light_blue_electrician_button);
                    deleteTypeFromSelecteds(PROFESSIONAL_TYPE.ELECTRICIAN);
                } else if (!electrician_pressed) {
                    electrician_pressed = true;
                    ib_electrician.setImageResource(R.mipmap.dark_blue_electrician_button);
                    addTypeToSelecteds(PROFESSIONAL_TYPE.ELECTRICIAN);
                }
            }
        });

        ib_fitter = (ImageButton) findViewById(R.id.ib_fitter);
        ib_fitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fitter_pressed) {
                    fitter_pressed = false;
                    ib_fitter.setImageResource(R.mipmap.light_blue_fitter_button);
                    deleteTypeFromSelecteds(PROFESSIONAL_TYPE.FITTER);
                } else if (!fitter_pressed) {
                    fitter_pressed = true;
                    ib_fitter.setImageResource(R.mipmap.dark_blue_fitter_button);
                    addTypeToSelecteds(PROFESSIONAL_TYPE.FITTER);
                }
            }
        });

        ib_plumber = (ImageButton) findViewById(R.id.ib_plumber);
        ib_plumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plumber_pressed) {
                    plumber_pressed = false;
                    ib_plumber.setImageResource(R.mipmap.light_blue_plumber_button);
                    deleteTypeFromSelecteds(PROFESSIONAL_TYPE.PLUMBER);
                } else if (!plumber_pressed) {
                    plumber_pressed = true;
                    ib_plumber.setImageResource(R.mipmap.dark_blue_plumber_button);
                    addTypeToSelecteds(PROFESSIONAL_TYPE.PLUMBER);
                }
            }
        });

    }

    private void loadProfessionalsSelected() {
        professionalsSelected = new ArrayList<Professional>();
        for (PROFESSIONAL_TYPE type : typesList) {
            for (Professional professional: professionals) {
                if(professional.getType().equals(type)) {
                    professionalsSelected.add(professional);
                }
            }
        }
        loadLocationsOnMap();
    }


    private void addTypeToSelecteds(PROFESSIONAL_TYPE type) {
        typesList.add(type);
        loadProfessionalsSelected();
    }

    private void deleteTypeFromSelecteds(PROFESSIONAL_TYPE type) {
        typesList.remove(type);
        loadProfessionalsSelected();
    }


    private List<Professional> criaProfissionais() {
        List<Professional> professionals = new ArrayList<Professional>();
        User user = new User(new Location("Rua das Uburanas, Campina Grande"),"Maria");
        Professional elec = new Professional();
        elec.setName("José Luiz");
        elec.setType(PROFESSIONAL_TYPE.ELECTRICIAN);
        elec.setLocation(new Location("Rua Rodrigues Alves Campina Grande"));
        elec.setCpf("486.136.632-14");
        elec.setPhone_number("(83)98645-4545");
        elec.addEvaluation(user, 3);
        professionals.add(elec);

        Professional plum = new Professional();
        plum.setName("João Melo");
        plum.setType(PROFESSIONAL_TYPE.PLUMBER);
        plum.setLocation(new Location("Avenida Juvênio Arruda Campina Grande"));
        plum.setCpf("576.373.113-17");
        plum.setPhone_number("(83)98645-8888");
        plum.addEvaluation(user, 4);
        professionals.add(plum);

        Professional fitter = new Professional();
        fitter.setName("Severino Miguel");
        fitter.setType(PROFESSIONAL_TYPE.FITTER);
        fitter.setLocation(new Location("Avenida Dr. Francisco Pinto Campina Grande"));
        fitter.setCpf("051.276.452-20");
        fitter.setPhone_number("(83)98645-8080");
        fitter.addEvaluation(user, 5);
        professionals.add(fitter);

        return professionals;
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        mapFragment.placeMarker(latLng);
        //mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
        mapFragment.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
    }

    public LatLng getLatLng(String location, Professional professional){

        List<Address> addressList = null;
        if(location != null || !location.equals(""))
        {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location , 1);
                Address address = addressList.get(0);
                professional.getLocation().setLatitude(address.getLatitude());
                professional.getLocation().setLatitude(address.getLongitude());
                LatLng latLng = new LatLng(address.getLatitude() , address.getLongitude());
                return latLng;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
        loadProfessionalsSelected();
    }

    private void loadLocationsOnMap() {
        mapFragment.getMap().clear();
        if (mLastLocation == null) {
            mLastLocation =  LocationServices.FusedLocationApi.getLastLocation (
                    mGoogleApiClient );
        }

        if  ( mLastLocation !=  null )  {
//            for (Professional p : professionalsSelected) {
//                handleLocationsProfessionals(p);
//            }
            setUpProfessionalsMarker();
            handleNewLocation(mLastLocation);

        } else {
            Log.i("MY LOCATION", "NULL");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    protected  void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected  void onStop ()  {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

    }


    private void setUpProfessionalsMarker() {

        professionalMarkerMap = new HashMap<Marker, Professional>();
        Marker marker;
        LatLng latLng;
        for (Professional prof: professionalsSelected) {
            if (prof.getLocation().getLongitude() == 0d || prof.getLocation().getLatitude() == 0d) {
                latLng = getLatLng(prof.getLocation().getProvider(), prof);
            } else {
                latLng = new LatLng(prof.getLocation().getLatitude(),prof.getLocation().getLongitude());
            }
            marker = mapFragment.placeMarker(prof, latLng);
            professionalMarkerMap.put(marker, prof);
        }

        mapFragment.getMap().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                final Professional professionalInfo = professionalMarkerMap.get(marker);
                infoWindow = null;

                if (!marker.getTitle().equals("Minha localização")) {
                    infoWindow = getLayoutInflater().inflate(R.layout.infowindow_professional, null);

                    tv_profession = (TextView) infoWindow.findViewById(R.id.tv_profession);
                    tv_profession.setText(professionalInfo.getType().getType());

                    tv_name_professional = (TextView) infoWindow.findViewById(R.id.tv_name_professional);
                    tv_name_professional.setText(professionalInfo.getName());

                    tv_cpf = (TextView) infoWindow.findViewById(R.id.tv_cpf);
                    tv_cpf.setText(professionalInfo.getCpf());

                    tv_phone_number = (TextView) infoWindow.findViewById(R.id.tv_phone_number);
                    tv_phone_number.setText(professionalInfo.getPhone_number());


                    RatingBar rate_bar = (RatingBar) infoWindow.findViewById(R.id.evaluataion_bar);

                    rate_bar.setRating(professionalInfo.getAverageEvaluations());
                    Log.d("Rating", professionalInfo.getAverageEvaluations()+"");

                    btn_call = (Button) infoWindow.findViewById(R.id.btn_call);
                    btn_call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView tv = (TextView) infoWindow.findViewById(R.id.tv_phone_number);
                            Log.d("CLICK", "Clicou!");

                            String number = tv.getText().toString();


                            Uri uri = Uri.parse("tel:" + number);
                            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                            startActivity(intent);
                        }
                    });
                }
                return infoWindow;
            }
        });

    }
}
