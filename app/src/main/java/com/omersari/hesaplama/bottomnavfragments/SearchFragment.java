package com.omersari.hesaplama.bottomnavfragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.omersari.hesaplama.database.ApiRecipeDao;
import com.omersari.hesaplama.database.ApiRecipeDatabase;
import com.omersari.hesaplama.database.LocalDataManager;
import com.omersari.hesaplama.database.NetworkUtils;
import com.omersari.hesaplama.databinding.FragmentSearchBinding;
import com.omersari.hesaplama.model.ApiResponseRecipe;
import com.omersari.hesaplama.model.Ingredient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SearchFragment extends Fragment {


    TextView recipeText;
    FragmentSearchBinding binding;

    LocalDataManager localDataManager;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;

    ApiRecipeDatabase db;
    ApiRecipeDao dao;

    CompositeDisposable compositeDisposable;
    ArrayList<Ingredient> ingredientArrayList;
    String email;
    String ingredients = "";

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient().newBuilder()
            .readTimeout(60, TimeUnit.SECONDS).build();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(getActivity(), ApiRecipeDatabase.class, "ApiResponseRecipe").build();
        dao = db.apiRecipeDao();
        compositeDisposable = new CompositeDisposable();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ingredientArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater,container,false);
        recipeText = binding.recipeText;
        email = auth.getCurrentUser().getEmail();
        getData();





        binding.imageButton.setOnClickListener((v)->{
            String question = ingredients + "Malzemelerini içinde barındıran bir yemek tarifi öner json formatında olsun Başlıkları => recipeName, cookTime, preparationTime, ingredients, preparation ";
            if(NetworkUtils.isNetworkAvailable(getActivity())){
                callAPI(question);
            }else{
                Toast.makeText(getActivity(), "Internet bağlantınız yok", Toast.LENGTH_SHORT).show();
            }

        });

        return binding.getRoot();
    }
    void addToChat(String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                JsonElement jsonElement = gson.fromJson(message, JsonElement.class);
                ApiResponseRecipe apiResponseRecipe = gson.fromJson(jsonElement, ApiResponseRecipe.class);
                compositeDisposable.add(dao.insert(apiResponseRecipe)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // Insertion success
                                    System.out.println("Insertion success");
                                },
                                throwable -> {
                                    // Handle insertion error
                                    System.err.println("Insertion error: " + throwable.getMessage());
                                }
                        ));
                recipeText.setText(apiResponseRecipe.getPreparation());

            }
        });
    }

    void addResponse(String response){
        addToChat(response);
    }

    void callAPI(String question){

        recipeText.setText("Lütfen Bekleyiniz...");
        //okhttp

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","gpt-3.5-turbo");

            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role","user");
            obj.put("content",question);
            messageArr.put(obj);

            jsonBody.put("messages", messageArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                        .getJSONObject("message")
                                                .getString("content");

                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    addResponse("Failed to load response due to "+response.body().string());
                }
            }
        });





    }


    private void getData() {

        firebaseFirestore.collection("Ingredients").whereArrayContains("whoAdded", email).orderBy("ingredientName", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println(error.getLocalizedMessage());
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        String id = snapshot.getId();
                        String name = (String) data.get("ingredientName");

                        Ingredient ingredient = new Ingredient();
                        ingredient.setId(id);
                        ingredient.setName(name);
                        ingredients = ingredients + ingredient.getName() + ", ";

                    }
                }
            }
        });

        /*
        firebaseFirestore.collection("Users").document(email).collection("Ingredients").orderBy("recipeName", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    System.err.println(error.getLocalizedMessage());
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if(value != null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();
                        String id = snapshot.getId();
                        String name = (String) data.get("recipeName");
                        String downloadUrl = (String) data.get("downloadurl");

                        Ingredient ingredient = new Ingredient();
                        ingredient.setId(id);
                        ingredient.setName(name);
                        System.out.println("ingreidnt = "+ ingredient.getName());
                        ingredients = ingredients + ingredient.getName() + ", " ;

                    }
                }
            }
        });

    };

         */
    }


}