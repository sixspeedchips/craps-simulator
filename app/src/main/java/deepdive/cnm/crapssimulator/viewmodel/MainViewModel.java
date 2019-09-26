package deepdive.cnm.crapssimulator.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.craps.model.Game;
import edu.cnm.deepdive.craps.model.Game.Round;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class MainViewModel extends AndroidViewModel {

  final private Random rng;
  final private MutableLiveData<Game> game;
  final private MutableLiveData<Round> round;

  public MainViewModel(@NonNull Application application) {
    super(application);
    rng = new SecureRandom();
    game = new MutableLiveData<>();
    round = new MutableLiveData<>();

    round.setValue(null);
    game.setValue(new Game(rng));
  }

  public LiveData<Game> getGame(){
    return game;
  }

  public LiveData<Round> getRound(){
    return round;
  }

  public void play(){
    round.setValue(Objects.requireNonNull(game.getValue()).play());

  }

  public void play(int rounds){
    //TODO start runner thread to play
    new Thread(()->{
      Round round = null;
      for (int i = 0; i < rounds; i++) {
        round = Objects.requireNonNull(game.getValue()).play();
      }
      MainViewModel.this.round.postValue(round);
    }).start();

  }

  public void reset(){
    Objects.requireNonNull(game.getValue()).reset();
    round.setValue(null);
  }

}
