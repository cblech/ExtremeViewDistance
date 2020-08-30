#version 410 core

// define the number of CPs in the output patch
layout (vertices = 3) out;

uniform vec3 uEyeWorldPos;

// attributes of the input CPs
in vec4 tcWorldPos[];
in vec4 tcColor[];
in vec2 tcTexCoord[];

// attributes of the output CPs
out vec4 teWorldPos[];
out vec4 teColor[];
out vec2 teTexCoord[];

float GetTessLevel(float Distance0, float Distance1,float DistanceBetween)
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

    return (400.0*DistanceBetween) / (AvgDistance * (1+AvgDistance*0.0015));
}

void main()
{
    float size = min(distance(tcWorldPos[1].xyz,tcWorldPos[2].xyz),distance(tcWorldPos[2].xyz,tcWorldPos[0].xyz));
    // Set the control points of the output patch
    teWorldPos[gl_InvocationID] = tcWorldPos[gl_InvocationID];
    teColor[gl_InvocationID] = tcColor[gl_InvocationID];
    teTexCoord[gl_InvocationID] = tcTexCoord[gl_InvocationID];
    //teColor[gl_InvocationID] = tcColor[gl_InvocationID];

    // Calculate the distance from the camera to the three control points
    float EyeToVertexDistance0 = distance(uEyeWorldPos, tcWorldPos[0].xyz);
    float EyeToVertexDistance1 = distance(uEyeWorldPos, tcWorldPos[1].xyz);
    float EyeToVertexDistance2 = distance(uEyeWorldPos, tcWorldPos[2].xyz);

    // Calculate the tessellation levels
    gl_TessLevelOuter[0] = GetTessLevel(EyeToVertexDistance1, EyeToVertexDistance2,size);
    gl_TessLevelOuter[1] = GetTessLevel(EyeToVertexDistance2, EyeToVertexDistance0,size);
    gl_TessLevelOuter[2] = GetTessLevel(EyeToVertexDistance0, EyeToVertexDistance1,size);
    gl_TessLevelInner[0] = gl_TessLevelOuter[2];
}