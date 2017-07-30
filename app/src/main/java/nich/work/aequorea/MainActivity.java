package nich.work.aequorea;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import nich.work.aequorea.model.mainpage.Page;
import nich.work.aequorea.network.NetworkService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        
        getMainPageInfo();
    }
    
    private void getMainPageInfo() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.cbnweek.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        
        NetworkService networkService = retrofit.create(NetworkService.class);
        networkService.getMainPageInfo(1)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Page>() {
                @Override
                public void accept(Page page) throws Exception {
                    Log.d("MainActivity", page.toString());
                }
            });
    }
}
