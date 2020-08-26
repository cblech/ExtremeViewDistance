#version 410 core

// define the number of CPs in the output patch
layout (vertices = 3) out;

uniform vec3 uEyeWorldPos;

// attributes of the input CPs
in vec4 tcWorldPos[];
in vec4 tcColor[];

// attributes of the output CPs
out vec4 teWorldPos[];
out vec4 teColor[];

float GetTessLevel(float Distance0, float Distance1)
{
    float AvgDistance = (Distance0 + Distance1) / 2.0;

    /*

    if (AvgDistance <= 2.0) {
        return 10.0;
    }
    else if (AvgDistance <= 5.0) {
        return 7.0;
    }
    else {
        return 3.0;
    }

    */

    return 4000.0 / AvgDistance;
}

void main()
{
    // Set the control points of the output patch
    teWorldPos[gl_InvocationID] = tcWorldPos[gl_InvocationID];
    teColor[gl_InvocationID] = vec4(distance(uEyeWorldPos, tcWorldPos[gl_InvocationID].xyz)/100.f);
    //teColor[gl_InvocationID] = tcColor[gl_InvocationID];

    // Calculate the distance from the camera to the three control points
    float EyeToVertexDistance0 = distance(uEyeWorldPos, tcWorldPos[0].xyz);
    float EyeToVertexDistance1 = distance(uEyeWorldPos, tcWorldPos[1].xyz);
    float EyeToVertexDistance2 = distance(uEyeWorldPos, tcWorldPos[2].xyz);

    // Calculate the tessellation levels
    gl_TessLevelOuter[0] = GetTessLevel(EyeToVertexDistance1, EyeToVertexDistance2);
    gl_TessLevelOuter[1] = GetTessLevel(EyeToVertexDistance2, EyeToVertexDistance0);
    gl_TessLevelOuter[2] = GetTessLevel(EyeToVertexDistance0, EyeToVertexDistance1);
    gl_TessLevelInner[0] = gl_TessLevelOuter[2];
}