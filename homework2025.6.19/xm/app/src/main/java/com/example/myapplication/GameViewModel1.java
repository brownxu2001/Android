package com.example.myapplication;

// GameViewModel1.java
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GameViewModel1 extends ViewModel {
    private GameRepository repository;
    private MutableLiveData<GameInfo> gameInfo = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public GameViewModel1() {
        repository = new GameRepository();
    }

    public void fetchGameInfo(String gameId) {
        isLoading.setValue(true);
        repository.getGameInfo(gameId).observeForever(apiResponse -> {
            isLoading.setValue(false);
            if (apiResponse.getCode() == 200) {
                gameInfo.setValue(apiResponse.getData());
            } else {
                errorMessage.setValue(apiResponse.getMsg());
            }
        });
    }

    public LiveData<GameInfo> getGameInfo() {
        return gameInfo;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
