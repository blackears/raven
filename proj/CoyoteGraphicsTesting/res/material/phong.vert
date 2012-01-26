uniform mat4 u_mvpMatrix;
uniform mat4 u_mvMatrix;
uniform mat4 u_mvITMatrix;

attribute vec3 a_position;
attribute vec3 a_normal;

varying vec3 v_pos;
varying vec3 v_norm;

void main()
{
   v_pos = (u_mvMatrix * vec4(a_position, 1)).xyz;
   v_norm = (u_mvITMatrix * vec4(a_normal, 0)).xyz;
   gl_Position = u_mvpMatrix * vec4(a_position, 1);
}