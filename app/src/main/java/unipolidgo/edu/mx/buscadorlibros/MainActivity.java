package unipolidgo.edu.mx.buscadorlibros;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public ShareActionProvider vShareActionProvider;
    private static final String QUERY_URL= "http://openlibrary.org/search.json?q=";
    Button btnBuscar ;
    EditText edtSugerir;
    ListView lstLista;
    AdaptadorJSON adaptador;
    FrameLayout barraContenedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar menu = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(menu);

        btnBuscar = (Button) this.findViewById(R.id.btnBuscar);
        adaptador = new AdaptadorJSON(this, getLayoutInflater());
        lstLista = (ListView) findViewById(R.id.lista);
        lstLista.setAdapter(adaptador);
        lstLista.setOnItemClickListener(this);
        edtSugerir = (EditText) findViewById(R.id.edtSugerir);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryBooks(edtSugerir.getText().toString());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem compartirItem = menu.findItem(R.id.menu_compartir);
        if(compartirItem != null){
            vShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(compartirItem);
        }

        setShareIntent();
        return true;
    }

    public void setShareIntent(){
        if(vShareActionProvider != null){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Desrrollo con Android");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Compartir");

            vShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void queryBooks(String frase){
        String url="";
        try {
            url= URLEncoder.encode(frase, "UTF-8");
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
            Toast.makeText(this, "Error:"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        barraContenedor = (FrameLayout) findViewById(R.id.barraProgresoContenedor);
        barraContenedor.setVisibility(View.VISIBLE);
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(QUERY_URL + url, new JsonHttpResponseHandler(){
            @Override
            public  void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Toast.makeText(getApplicationContext(), "OK!", Toast.LENGTH_LONG).show();
                Log.d("Resultados", response.toString());
                adaptador.updateData(response.optJSONArray("docs"));
                barraContenedor.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                Toast.makeText(getApplicationContext(), "Error: "+statusCode+" "+ throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("NOooooo!!", statusCode + " " + throwable.getMessage());
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long I) {
        JSONObject objeto = (JSONObject) adaptador.getItem(i);
        String cubiertaID = objeto.optString("cover_i", "");
        String autorLibro = objeto.optString("author_name", "");
        String tituloLibro = objeto.optString("title", "");
        String anoLibro = objeto.optString("first_publish_year", "");
        String casaAutora = objeto.optString("subject", "");

        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("cubiertaID", cubiertaID);
        detailIntent.putExtra("autorLibro", autorLibro);
        detailIntent.putExtra("tituloLibro", tituloLibro);
        detailIntent.putExtra("anoLibro", anoLibro);
        detailIntent.putExtra("casaAutora", casaAutora);
        startActivity(detailIntent);
    }
}
