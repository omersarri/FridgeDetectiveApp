package com.omersari.hesaplama.model;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private static UserManager instance;
    private User currentUser;
    private final String maleProfilePic = "https://firebasestorage.googleapis.com/v0/b/recipeapp-43843.appspot.com/o/defaultProfilePics%2Fmale_profile_pic.png?alt=media&token=c8bf8fb4-5ed7-438d-bbca-1fe0c9f502a1";
    private final String femaleProfilePic = "https://firebasestorage.googleapis.com/v0/b/recipeapp-43843.appspot.com/o/defaultProfilePics%2Ffemale_profile_pic.png?alt=media&token=3cd7f254-bb28-4bb7-9e44-3747402c5018";


    private UserManager() {

        currentUser = new User();
    }
    public interface CreateUserCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public interface LoginCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface GetUserInfoCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public interface UploadProfilePicCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }


    public void getUserInfo(GetUserInfoCallback getUserInfoCallback) {
        firebaseFirestore.collection("Users").whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> data = document.getData();
                                String name = (String) data.get("name");
                                String email = (String) data.get("email");
                                String gender = (String) data.get("gender");
                                String profilePic = (String) data.get("profilePic");


                                currentUser.setName(name);
                                currentUser.setEmail(email);
                                currentUser.setGender(gender);
                                currentUser.setProfilePic(profilePic);

                                getUserInfoCallback.onSuccess();


                            }
                        } else {
                            getUserInfoCallback.onFailure("Error getting documents.");
                        }

                    }
                });
    }

    public void login(String email, String password, LoginCallback loginCallback) {
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                loginCallback.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginCallback.onFailure(e.getLocalizedMessage());
            }
        });
    }


    public void createNewUser(String name, String email, String password,
                                String gender, CreateUserCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)// displayName'i ayarla
                                .build();

                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        HashMap<String, Object> postData = new HashMap<>();
                                        postData.put("name", name);
                                        postData.put("email", email);
                                        postData.put("gender", gender);
                                        if(gender.equals("Male")){postData.put("profilePic", maleProfilePic);
                                        }else if(gender.equals("Female")){postData.put("profilePic", femaleProfilePic);}
                                        firebaseFirestore.collection("Users").document(email).set(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                currentUser.setName(name);
                                                currentUser.setEmail(email);
                                                currentUser.setGender(gender);
                                                if(gender.equals("Male")){currentUser.setProfilePic(maleProfilePic);
                                                }else if(gender.equals("Female")){currentUser.setProfilePic(femaleProfilePic);}

                                                callback.onSuccess();


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                callback.onFailure(e.getLocalizedMessage());
                                            }
                                        });

                                    } else {
                                        // displayName güncelleme hatası
                                        callback.onFailure("displayName Update Error");
                                    }
                                });
                    } else {
                        // Kullanıcı oluşturma hatası
                        callback.onFailure("An error has occured");
                    }
                });
    }

    public void uploadProfilePic(Uri imageData, UploadProfilePicCallback uploadProfilePicCallback) {
        if(imageData != null) {

            UUID uuid = UUID.randomUUID();
            String imageName = currentUser.getEmail()+"/"+"profileImages/" + uuid + ".jpg";
            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Download url
                    StorageReference newReference = firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();

                            updateUserInfo("profilePic", downloadUrl);
                            currentUser.setProfilePic(downloadUrl);
                            uploadProfilePicCallback.onSuccess();


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadProfilePicCallback.onFailure(e.getLocalizedMessage());
                }
            });
        }
    }


    public void updateUserInfo(String string, Object object){
        Map<String, Object> newData = new HashMap<>();
        newData.put(string, object);
        firebaseFirestore.collection("Users").document(auth.getCurrentUser().getEmail())
                .update(newData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

}