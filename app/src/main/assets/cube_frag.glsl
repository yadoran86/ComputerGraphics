#version 300 es
precision mediump float;

uniform vec4 fColor;

out vec4 fragColor;

void main(){
    fragColor = fColor;
}