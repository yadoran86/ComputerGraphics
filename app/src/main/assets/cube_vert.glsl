#version 300 es

uniform mat4 uMVPMatrix, worldMat;

layout(location = 2) in vec4 vPosition;

void main(){
    gl_Position = uMVPMatrix * vPosition;
}