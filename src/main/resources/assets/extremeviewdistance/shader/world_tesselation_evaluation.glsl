#version 410 core

layout(triangles, fractional_even_spacing, ccw) in;

//uniform mat4 gVP;
//uniform sampler2D gDisplacementMap;
//uniform float gDispFactor;
uniform mat4 uViewProjectionMat;
uniform sampler2D uDepthSampler;

in vec4 teWorldPos[];
in vec4 teColor[];
in vec2 teTexCoord[];

out vec4 fColor;
out vec4 fWorldPos;
out vec2 fTexCoord;
out vec3 fNormal;


vec2 interpolate2D(vec2 v0, vec2 v1, vec2 v2)
{
    return vec2(gl_TessCoord.x) * v0 + vec2(gl_TessCoord.y) * v1 + vec2(gl_TessCoord.z) * v2;
}

vec3 interpolate3D(vec3 v0, vec3 v1, vec3 v2)
{
    return vec3(gl_TessCoord.x) * v0 + vec3(gl_TessCoord.y) * v1 + vec3(gl_TessCoord.z) * v2;
}
vec4 interpolate4D(vec4 v0, vec4 v1, vec4 v2)
{
    return gl_TessCoord.x * v0 + gl_TessCoord.y * v1 + gl_TessCoord.z * v2;
}

void main()
{
    // Interpolate the attributes of the output vertex using the barycentric coordinates
    fWorldPos = interpolate4D(teWorldPos[0], teWorldPos[1], teWorldPos[2]);
    fColor = interpolate4D(teColor[0], teColor[1], teColor[2]);
    fTexCoord = interpolate2D(teTexCoord[0], teTexCoord[1], teTexCoord[2]);

    float y = (fWorldPos.y+(texture(uDepthSampler,fTexCoord).r*255.f));

    fWorldPos = vec4(fWorldPos.x,y,fWorldPos.z,fWorldPos.w);

    fNormal = normalize(
        cross(
            vec3(0f,texture(uDepthSampler,fTexCoord+vec2(0,0.04)).r-texture(uDepthSampler,fTexCoord).r,1f),
            vec3(1f,texture(uDepthSampler,fTexCoord+vec2(0.04,0)).r-texture(uDepthSampler,fTexCoord).r,0f)));

    // Displace the vertex along the normal
    //float Displacement = texture(gDisplacementMap, TexCoord_FS_in.xy).x;
    //WorldPos_FS_in += Normal_FS_in * Displacement * gDispFactor;
    gl_Position =uViewProjectionMat * fWorldPos;

}

