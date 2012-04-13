uniform mat4 u_mvpMatrix;
uniform mat4 u_texMatrix;

attribute vec4 a_position;
attribute vec4 a_texCoord;

varying vec4 v_texCoord;
varying vec4 v_pos;

void main()
{
    gl_Position = u_mvpMatrix * a_position;
    v_pos = a_position;
    v_texCoord = u_texMatrix * a_position;
}