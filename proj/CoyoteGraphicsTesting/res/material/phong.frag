uniform vec3 u_lightPos;
uniform vec4 u_colorDif;
uniform vec4 u_colorSpec;
uniform float u_shininess;

varying vec3 v_pos;
varying vec3 v_norm;

void main()
{
    vec3 lightDir = u_lightPos - v_pos;
    vec3 lightNorm = normalize(lightDir);

    float lumDif = dot(lightNorm, v_norm);

    vec3 viewDirUnit = normalize(-v_pos);
    vec3 halfVec = normalize(lightNorm + viewDirUnit);
    float lumSpec = pow(clamp(dot(halfVec, v_norm), 0.0, 1.0), u_shininess);

	gl_FragColor = vec4(u_colorDif.xyz * lumDif + u_colorSpec.xyz * lumSpec, 1);
}