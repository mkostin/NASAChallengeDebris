package com.me.mygdxgame;

import com.akvelon.hackathon.Array2;
import com.akvelon.hackathon.Coordinate;
import com.akvelon.hackathon.Placemark;
import com.akvelon.hackathon.ThermoSolver;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MyGDXSattelite implements ApplicationListener {
    public OrthographicCamera cam;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    public Environment enviroment;
    public CameraInputController camController;
    public DirectionalLight light;
    public FrameBuffer frameBuffer;
    public ScreenUtils screenUtils;
    public List<Placemark> debris;
    public List<Placemark> casket;
    public Model debrisModel;
    public List<ModelInstance> debrisInstance;
    public List<ModelInstance> casketInstance;
    public ModelInstance spaceInstance;
    public Model space;

    private boolean useCasket = false;

    @Override
    public void create() {
        debrisInstance = new ArrayList<ModelInstance>();
        casketInstance = new ArrayList<ModelInstance>();
        debris = Main.getDebris();
        casket = debris.subList(0, 100);

        light = new DirectionalLight().set(222f, 222f, 222f, 300f, 300f, 300f);
        enviroment = new Environment();
        enviroment.set(new ColorAttribute(ColorAttribute.AmbientLight, 2.4f, 3.4f, 2.4f, 3f));
        enviroment.add(light);

        modelBatch = new ModelBatch();

        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        cam.rotate(90, 0, 0, 1);
        cam.position.set(50f, 50f,50f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 4000f;
        cam.update();

        ModelBuilder modelBuilder = new ModelBuilder();
        ObjLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("textures/Earth.obj"));
        //
//        model = modelBuilder.createSphere(128f, 128f, 128f, 20, 20,
//                new Material(ColorAttribute.AmbientAlias),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        instance = new ModelInstance(model);
        instance.transform.scale(128, 128, 128);
        instance.transform.rotate(1, 0, 0, 180);
        instance.transform.rotate(0, 0, 1, 90);

        space = loader.loadModel(Gdx.files.internal("textures/space/spacesphere.obj"));
        spaceInstance = new ModelInstance(space);
        spaceInstance.transform.scale(10,10,10);

        for (int i = 0; i < 100; i++) {
            debrisModel = loader.loadModel(Gdx.files.internal("textures/Nuka_Cola_Fridge.obj"));
            casketInstance.add(new ModelInstance(debrisModel));
        }

        for (Placemark pm : debris) {
            Random rand = new Random();
            debrisModel = modelBuilder.createSphere(3f, 3f, 3f, 4, 4,
                    new Material(ColorAttribute.createDiffuse(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), rand.nextFloat())),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
            ModelInstance mi = new ModelInstance(debrisModel);
            Coordinate coordinate = pm.getNextCoordinate(100f);
            mi.transform.translate((float) coordinate.x, (float) coordinate.y, (float) coordinate.z);
            debrisInstance.add(mi);
        }
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
    }

    int q = 0;

    @Override
    public void render() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cam.zoom += 0.01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cam.zoom -= 0.01f;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            useCasket = !useCasket;
        }
        cam.update();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(cam);
        instance.transform.rotate(0, 1, 0.1f, 1);

        if (useCasket) {
            if (q % 3 == 0) {
                for (int i = 0; i < casket.size(); i++) {
                    ModelInstance mi = casketInstance.get(i);
                    Coordinate nextCoordinate = casket.get(i).getNextCoordinate(100f);
                    mi.transform.translate((float) nextCoordinate.x - mi.transform.getValues()[12],
                            (float) nextCoordinate.y - mi.transform.getValues()[13],
                            (float) nextCoordinate.z - mi.transform.getValues()[14]);
                    modelBatch.render(mi, enviroment);
                }
            } else {
                modelBatch.render(casketInstance, enviroment);
            }
        } else {
            if (q % 3 == 0) {
                for (int i = 0; i < debris.size(); i++) {
                    ModelInstance mi = debrisInstance.get(i);
                    Coordinate nextCoordinate = debris.get(i).getNextCoordinate(100f);
                    mi.transform.translate((float) nextCoordinate.x - mi.transform.getValues()[12],
                            (float) nextCoordinate.y - mi.transform.getValues()[13],
                            (float) nextCoordinate.z - mi.transform.getValues()[14]);
                    modelBatch.render(mi, enviroment);
                }
            } else {
                modelBatch.render(debrisInstance, enviroment);
            }
        }
        q++;
        modelBatch.render(spaceInstance, enviroment);
        modelBatch.render(instance, enviroment);
        modelBatch.end();
    }


    @Override
    public void dispose() {
        getFrame();
        modelBatch.dispose();
        model.dispose();
    }

    public void getFrame() {
       byte[] bytes = ScreenUtils.getFrameBufferPixels(0, 0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),false );

    }

    @Override
    public void resize(int width, int height) {
    }


    @Override
    public void pause() {
    }


    @Override
    public void resume() {
    }

    public enum EvalMethod {
        SIMPLE, CRANK_NICOLSON;
    }

    public static void main(String[] args) {

        EvalMethod method = EvalMethod.CRANK_NICOLSON;
        ThermoSolver solver = new ThermoSolver(1.0);

        long step = 0;
        final long MAX_STEP = 1000;
        while (step < MAX_STEP) {
            process(method, solver);
            step++;
        }
    }

    public static void process(EvalMethod method, ThermoSolver solver) {
        try {
            // ht2d.computeNext();
            switch (method) {
                case SIMPLE:
                    solver.simpleExplicitNext();
                    break;
                case CRANK_NICOLSON:
                    solver.crankNicolsonNext();
                    break;
            }

            try {
                Thread.sleep(250);
            } catch (Exception e) {
            }
            Array2 layer = solver.timeLayer.get(solver.timeLayer.size() - 1);

            // Visualize

        } catch (Exception ex) {
        }
    }

}
