uniform vec4 u_colorFg;
uniform vec4 u_colorBg;
uniform float u_lineWidth;

varying vec4 v_pos;

void main()
{
    bool gradX = fract(v_pos.x / u_lineWidth) < .5;
    bool gradY = fract(v_pos.y / u_lineWidth) < .5;

	gl_FragColor = ((gradX && gradY) || (!gradX && !gradY)) 
        ? u_colorFg : u_colorBg;
}
