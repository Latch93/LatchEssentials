package lmp;

import lmp.constants.Constants;
import org.bukkit.Bukkit;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import java.util.Locale;
import java.util.Objects;

public class TextToSpeech {

    public static void SayMessage(){
        Bukkit.getScheduler().runTaskAsynchronously(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(Constants.PLUGIN_NAME)), () -> {
            try {
                // Set property as Kevin Dictionary
                System.setProperty(
                        "freetts.voices",
                        "com.sun.speech.freetts.en.us"
                                + ".cmu_us_kal.KevinVoiceDirectory");

                // Register Engine
                Central.registerEngineCentral(
                        "com.sun.speech.freetts"
                                + ".jsapi.FreeTTSEngineCentral");

                // Create a Synthesizer
                Synthesizer synthesizer
                        = Central.createSynthesizer(
                        new SynthesizerModeDesc(Locale.US));

                // Allocate synthesizer
                synthesizer.allocate();

                // Resume Synthesizer
                synthesizer.resume();

                // Speaks the given text
                // until the queue is empty.
                synthesizer.speakPlainText(
                        "Geeks for Geeks", null);
                synthesizer.waitEngineState(
                        Synthesizer.QUEUE_EMPTY);

                // Deallocate the Synthesizer.
                synthesizer.cancelAll();
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}