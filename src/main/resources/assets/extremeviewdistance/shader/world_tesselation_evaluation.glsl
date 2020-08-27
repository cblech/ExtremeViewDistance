#version 410 core

layout(triangles, fractional_even_spacing, ccw) in;

//uniform mat4 gVP;
//uniform sampler2D gDisplacementMap;
//uniform float gDispFactor;
uniform mat4 uViewProjectionMat;

in vec4 teWorldPos[];
in vec4 teColor[];

out vec4 fColor;
out vec4 fWorldPos;


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

    // Displace the vertex along the normal
    //float Displacement = texture(gDisplacementMap, TexCoord_FS_in.xy).x;
    //WorldPos_FS_in += Normal_FS_in * Displacement * gDispFactor;
    gl_Position = uViewProjectionMat * fWorldPos;
    gl_Position = vec4(gl_Position.x,gl_Position.y,gl_Position.z,gl_Position.w);
}

