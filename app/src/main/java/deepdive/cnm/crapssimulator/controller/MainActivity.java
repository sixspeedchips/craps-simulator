package deepdive.cnm.crapssimulator.controller;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import deepdive.cnm.crapssimulator.R;
import deepdive.cnm.crapssimulator.view.RoundAdapter;
import edu.cnm.deepdive.craps.model.Game;
import edu.cnm.deepdive.craps.model.Game.Round;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

  private Boolean running = false;
  private Game game;
  private Random rng;
  private RoundAdapter adapter;
  private TextView tally;
  private ListView rolls;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tally = findViewById(R.id.tally);
    rolls = findViewById(R.id.rolls);
    adapter = new RoundAdapter(this);
    rolls.setAdapter(adapter);
    rng = new Random();
    resetGame();


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.options, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    menu.findItem(R.id.play_one).setVisible(!running);
    menu.findItem(R.id.fast_forward).setVisible(!running);
    menu.findItem(R.id.pause).setVisible(running);
    menu.findItem(R.id.reset).setEnabled(!running);
    return true;

  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.play_one:
        updateDisplay(game.play(),game.getWins(),game.getLosses(),game.getPercentage());
        break;
      case R.id.reset:
        resetGame();
        break;
      case R.id.fast_forward:
        new Runner().start();
        invalidateOptionsMenu();
        running = true;
        break;
      case R.id.pause:
        running = false;
        break;
      default:
        handled = super.onOptionsItemSelected(item);

    }

    return handled;
  }

  private void updateDisplay(Round round, int wins, int plays, double percentage) {
    adapter.add(round);
    tally.setText(getString(R.string.tally_format, wins, plays, 100 * percentage));
  }

  private void resetGame(){
    game = new Game(rng);
    updateDisplay(null,0,0,0);
  }

  private class Runner extends Thread{

    private int plays;
    private int wins;
    private double percentage;
    private Round round;

    @Override
    public void run() {

      while (running){
        round = game.play();

        if(game.getPlays() % 500000 == 0){
          wins = game.getWins();
          plays = game.getPlays();
          percentage = game.getPercentage();
          runOnUiThread(new Updater(round,wins,plays,percentage));
        }

      }
      wins = game.getWins();
      plays = game.getPlays();
      percentage = game.getPercentage();
      runOnUiThread(new Updater(round,wins,plays,percentage));
      invalidateOptionsMenu();

    }
  }

  private class Updater implements Runnable{

    private final Round round;
    private final int wins;
    private final int plays;
    private final double percentage;

    public Updater(Round round, int wins, int plays, double percentage) {
      this.round = round;
      this.wins = wins;
      this.plays = plays;
      this.percentage = percentage;
    }

    @Override
    public void run() {
      updateDisplay(round,wins,plays,percentage);

    }
  }

}
