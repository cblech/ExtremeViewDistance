#version 330

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;

out vec4 tcColor;
out vec4 tcWorldPos;

uniform mat4 uModelMat;
//uniform mat4 uViewProjectionMat;

void main()
{
    tcColor = aColor;
    tcWorldPos = uModelMat * vec4(aPos, 1.0);
}
