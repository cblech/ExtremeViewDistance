#version 330
in vec4 fColor;
in vec4 fWorldPos;
in vec2 fTexCoord;
in vec3 fNormal;

out vec4 FragColor;

uniform sampler2D uDepthSampler;
uniform vec3 uEyeWorldPos;

void main()
{
    vec4 t = texture(uDepthSampler,fTexCoord);

    //float dt = dot(fNormal,uEyeWorldPos);

    //FragColor =vec4(fNormal.r,fNormal.g,fNormal.b,1.f);
    float v = fWorldPos.y/25 - 2;

    FragColor = vec4(v,v,v,1);
}
