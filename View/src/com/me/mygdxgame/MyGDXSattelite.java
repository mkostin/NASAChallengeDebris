package com.me.mygdxgame;

import com.akvelon.hackathon.Placemark;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g3d.Material;


public class MyGDXSattelite implements ApplicationListener {
    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;
    public Environment enviroment;
    public CameraInputController camController;
    public DirectionalLight light;
    public FrameBuffer frameBuffer;
    public ScreenUtils screenUtils;
    public List<Placemark> debris;
    public Model debrisModel;
    public List<ModelInstance> debrisInstance;
    public BoundingBox bounds;
    public Texture texture;
    public Mesh earthMesh;
    public Cubemap cubemap;



    @Override
    public void create() {

        Gdx.graphics.setContinuousRendering(false);
        Gdx.graphics.requestRendering();

        texture = new Texture(Gdx.files.internal("textures/eath_texture.png"));

        debrisInstance = new ArrayList<ModelInstance>();
        debris = Main.getDebris();

        light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, 300f, 300f, 300f);
        enviroment = new Environment();
        enviroment.set(new ColorAttribute(ColorAttribute.AmbientLight, 2.4f, 3.4f, 2.4f, 3f));
        enviroment.add(light);

//        Texture texture = new Texture("textures/eath_texture.png");
        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(150f, 150f, 150f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 1300f;
        cam.update();
        ObjLoader loader = new ObjLoader();
        ModelBuilder modelBuilder = new ModelBuilder();
        model = loader.loadModel(Gdx.files.internal("textures/Earth.obj"));

       // Material mat = new Material("mat", new TextureAttribute(TextureAttribute.Bump, texture));
//        model = modelBuilder.createSphere(128f, 128f, 128f, 20, 20,
//                mat,
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        instance = new ModelInstance(model);
        instance.transform.scale(100f,100f,100f);
        System.out.println();

        for (Placemark pm : debris) {

            debrisModel = loader.loadModel(Gdx.files.internal("textures/imac/imac.obj"));
            ModelInstance mi = new ModelInstance(debrisModel);

            mi.transform.scale(5f,5f,5f);
            debrisInstance.add(mi);
            mi.transform.translate((float) pm.coordinate.x / 100f, (float) pm.coordinate.y / 100f, (float) pm.coordinate.z / 100f);
        }
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
    }


    @Override
    public void render() {
        cam.update();
        cam.apply(Gdx.gl10);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glActiveTexture(0);
        modelBatch.begin(cam);
        instance.transform.rotate(0.2f, -0.2f, 0.2f, 0.11f);
        Gdx.gl.glEnable(GL11.GL_MULTISAMPLE);
        Gdx.gl.glActiveTexture(GL10.GL_TEXTURE);
        Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);
        texture.bind();

        modelBatch.render(instance, enviroment);
        modelBatch.render(debrisInstance, enviroment);

        modelBatch.end();

    }


    @Override
    public void dispose() {

        modelBatch.dispose();
        model.dispose();
    }

    public byte[] getFrame() {

        byte[] bytes = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        return bytes;
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

}
