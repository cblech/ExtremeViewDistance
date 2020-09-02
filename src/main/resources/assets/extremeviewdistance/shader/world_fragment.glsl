#version 330
in vec4 fColor;
in vec4 fWorldPos;
in vec2 fTexCoord;
in vec3 fNormal;

out vec4 FragColor;

uniform sampler2D uDepthSampler;
uniform sampler2D uColorSampler;
uniform vec3 uEyeWorldPos;

void main()
{
    //vec4 t = texture(uDepthSampler,fTexCoord);

    //float dt = dot(fNormal,uEyeWorldPos);


    //FragColor =vec4(fNormal.r,fNormal.g,fNormal.b,1.f);
    //float v = fWorldPos.y/60 -1;

    float threshhold = 0.98;

    if( fNormal.r<threshhold&&fNormal.r>-threshhold&&
        fNormal.b<threshhold&&fNormal.b>-threshhold)
    {
        //FragColor =vec4(1,1,1,1.f);
        FragColor = texture(uColorSampler,fTexCoord);
    }else{
        discard;
    }

    // vec4(v,v,v,1);
}
