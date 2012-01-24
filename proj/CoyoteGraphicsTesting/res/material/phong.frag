#version 140

uniform vec3 u_lightPos;
uniform vec4 u_colorDif = vec4(1, 0, 0, 1);
uniform vec4 u_colorSpec = vec4(1, 1, 1, 1);
uniform float u_shininess = 20;

varying vec3 v_pos;
varying vec3 v_norm;

void main()
{
    vec3 lightDir = u_lightPos - v_pos;
    vec3 lightNorm = normalize(lightDir);

    float lumDif = dot(lightNorm, v_norm);

    vec3 viewDirUnit = normalize(-v_pos);
    vec3 halfVec = normalize(lightNorm + viewDirUnit);
    float lumSpec = dot(halfVec, v_norm);
    lumSpec = pow(lumSpec, u_shininess);

	gl_FragColor = vec4(u_colorDif.xyz * lumDif + u_colorSpec.xyz * lumSpec, 1);
}