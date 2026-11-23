package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs.CustomPhatom;

public class CustomPhatomModel<T extends CustomPhatom> extends HierarchicalModel<T> {

    private static final String TAIL_BASE = "tail_base";
    private static final String TAIL_TIP  = "tail_tip";

    private final ModelPart root;
    private final ModelPart leftWingBase;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingBase;
    private final ModelPart rightWingTip;
    private final ModelPart tailBase;
    private final ModelPart tailTip;

    public CustomPhatomModel(ModelPart modelPart) {
        this.root = modelPart;
        ModelPart body      = modelPart.getChild("body");
        this.tailBase       = body.getChild("tail_base");
        this.tailTip        = this.tailBase.getChild("tail_tip");
        this.leftWingBase   = body.getChild("left_wing_base");
        this.leftWingTip    = this.leftWingBase.getChild("left_wing_tip");
        this.rightWingBase  = body.getChild("right_wing_base");
        this.rightWingTip   = this.rightWingBase.getChild("right_wing_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 8)
                        .addBox(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F),
                PartPose.rotation(-0.1F, 0.0F, 0.0F));

        PartDefinition tailBase = body.addOrReplaceChild("tail_base",
                CubeListBuilder.create().texOffs(3, 20)
                        .addBox(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F),
                PartPose.offset(0.0F, -2.0F, 1.0F));
        tailBase.addOrReplaceChild("tail_tip",
                CubeListBuilder.create().texOffs(4, 29)
                        .addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F),
                PartPose.offset(0.0F, 0.5F, 6.0F));

        PartDefinition leftWingBase = body.addOrReplaceChild("left_wing_base",
                CubeListBuilder.create().texOffs(23, 12)
                        .addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F),
                PartPose.offsetAndRotation(2.0F, -2.0F, -8.0F, 0.0F, 0.0F, 0.1F));
        leftWingBase.addOrReplaceChild("left_wing_tip",
                CubeListBuilder.create().texOffs(16, 24)
                        .addBox(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F),
                PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1F));

        PartDefinition rightWingBase = body.addOrReplaceChild("right_wing_base",
                CubeListBuilder.create().texOffs(23, 12).mirror()
                        .addBox(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F),
                PartPose.offsetAndRotation(-3.0F, -2.0F, -8.0F, 0.0F, 0.0F, -0.1F));
        rightWingBase.addOrReplaceChild("right_wing_tip",
                CubeListBuilder.create().texOffs(16, 24).mirror()
                        .addBox(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F),
                PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1F));

        body.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, -7.0F, 0.2F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // getUniqueFlapTickOffset() viene de FlyingMob — no existe, usamos ageInTicks directo
        float k = ageInTicks * 7.448451F * ((float) Math.PI / 180F);
        this.leftWingBase.zRot  =  Mth.cos(k) * 16.0F * ((float) Math.PI / 180F);
        this.leftWingTip.zRot   =  Mth.cos(k) * 16.0F * ((float) Math.PI / 180F);
        this.rightWingBase.zRot = -this.leftWingBase.zRot;
        this.rightWingTip.zRot  = -this.leftWingTip.zRot;
        this.tailBase.xRot = -(5.0F + Mth.cos(k * 2.0F) * 5.0F) * ((float) Math.PI / 180F);
        this.tailTip.xRot  = -(5.0F + Mth.cos(k * 2.0F) * 5.0F) * ((float) Math.PI / 180F);
    }
}
