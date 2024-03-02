package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.game.Game2D;
import com.mygdx.game.input.GameKeys;

public class LoadingUI extends Table {
    private final String loadingScreen;

    private final ProgressBar progressBar;
    private final TextButton textButton;
    private final TextButton pressAnyKeyButton;


    public LoadingUI(final Game2D context) {
        super(context.getSkin());
        setFillParent(true);

        final I18NBundle i18NBundle = context.getI18NBundle();

        progressBar = new ProgressBar(0, 1, 0.01f, false, getSkin(), "default");
        progressBar.setAnimateDuration(1);

        loadingScreen = i18NBundle.format("loading");
        textButton = new TextButton(loadingScreen, getSkin(), "huge");
        textButton.getLabel().setWrap(true);

        pressAnyKeyButton = new TextButton(i18NBundle.format("pressAnyKey"),getSkin(),"normal");
        pressAnyKeyButton.getLabel().setWrap(true);
        pressAnyKeyButton.setVisible(false);
        pressAnyKeyButton.addListener(new InputListener(){;//scene2d
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                context.getInputManager().notifyKeyDown(GameKeys.SELECT);
                return true;
            }
        });


        add(pressAnyKeyButton).expand().fill().center().row();
        add(textButton).expandX().fillX().bottom().row();
        add(progressBar).expandX().fillX().bottom().pad(20, 25, 20, 25);
    }

    public void setProgress(final  float progress){
        progressBar.setValue(progress);

        textButton.setText("bla");
        final StringBuilder stringBuilder = textButton.getLabel().getText();
        stringBuilder.setLength(0);

        stringBuilder.append(loadingScreen);
        stringBuilder.append(" (");
        stringBuilder.append(progress*100);
        stringBuilder.append("% )");
        textButton.getLabel().invalidateHierarchy();

        if(progress >= 1 && !pressAnyKeyButton.isVisible()) {
            pressAnyKeyButton.setVisible(true);

            pressAnyKeyButton.setColor(1, 1, 1, 0);
            pressAnyKeyButton.addAction(Actions.forever(Actions.sequence(Actions.alpha(1, 1), Actions.alpha(0, 1))));
        }

    }
}
